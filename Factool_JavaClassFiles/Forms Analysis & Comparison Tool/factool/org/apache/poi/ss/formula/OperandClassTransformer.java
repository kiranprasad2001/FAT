// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula;

import org.apache.poi.ss.formula.ptg.AttrPtg;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.RangePtg;
import org.apache.poi.ss.formula.ptg.UnionPtg;
import org.apache.poi.ss.formula.ptg.MemAreaPtg;
import org.apache.poi.ss.formula.ptg.MemFuncPtg;
import org.apache.poi.ss.formula.ptg.ControlPtg;
import org.apache.poi.ss.formula.ptg.ValueOperatorPtg;
import org.apache.poi.ss.formula.ptg.FuncVarPtg;
import org.apache.poi.ss.formula.ptg.AbstractFunctionPtg;

final class OperandClassTransformer
{
    private final int _formulaType;
    
    public OperandClassTransformer(final int formulaType) {
        this._formulaType = formulaType;
    }
    
    public void transformFormula(final ParseNode rootNode) {
        byte rootNodeOperandClass = 0;
        switch (this._formulaType) {
            case 0: {
                rootNodeOperandClass = 32;
                break;
            }
            case 2: {
                rootNodeOperandClass = 64;
                break;
            }
            case 4:
            case 5: {
                rootNodeOperandClass = 0;
                break;
            }
            default: {
                throw new RuntimeException("Incomplete code - formula type (" + this._formulaType + ") not supported yet");
            }
        }
        this.transformNode(rootNode, rootNodeOperandClass, false);
    }
    
    private void transformNode(final ParseNode node, final byte desiredOperandClass, final boolean callerForceArrayFlag) {
        Ptg token = node.getToken();
        final ParseNode[] children = node.getChildren();
        final boolean isSimpleValueFunc = isSimpleValueFunction(token);
        if (isSimpleValueFunc) {
            final boolean localForceArray = desiredOperandClass == 64;
            for (int i = 0; i < children.length; ++i) {
                this.transformNode(children[i], desiredOperandClass, localForceArray);
            }
            this.setSimpleValueFuncClass((AbstractFunctionPtg)token, desiredOperandClass, callerForceArrayFlag);
            return;
        }
        if (isSingleArgSum(token)) {
            token = FuncVarPtg.SUM;
        }
        if (token instanceof ValueOperatorPtg || token instanceof ControlPtg || token instanceof MemFuncPtg || token instanceof MemAreaPtg || token instanceof UnionPtg) {
            final byte localDesiredOperandClass = (byte)((desiredOperandClass == 0) ? 32 : desiredOperandClass);
            for (int i = 0; i < children.length; ++i) {
                this.transformNode(children[i], localDesiredOperandClass, callerForceArrayFlag);
            }
            return;
        }
        if (token instanceof AbstractFunctionPtg) {
            this.transformFunctionNode((AbstractFunctionPtg)token, children, desiredOperandClass, callerForceArrayFlag);
            return;
        }
        if (children.length > 0) {
            if (token == RangePtg.instance) {
                return;
            }
            throw new IllegalStateException("Node should not have any children");
        }
        else {
            if (token.isBaseToken()) {
                return;
            }
            token.setClass(this.transformClass(token.getPtgClass(), desiredOperandClass, callerForceArrayFlag));
        }
    }
    
    private static boolean isSingleArgSum(final Ptg token) {
        if (token instanceof AttrPtg) {
            final AttrPtg attrPtg = (AttrPtg)token;
            return attrPtg.isSum();
        }
        return false;
    }
    
    private static boolean isSimpleValueFunction(final Ptg token) {
        if (!(token instanceof AbstractFunctionPtg)) {
            return false;
        }
        final AbstractFunctionPtg aptg = (AbstractFunctionPtg)token;
        if (aptg.getDefaultOperandClass() != 32) {
            return false;
        }
        final int numberOfOperands = aptg.getNumberOfOperands();
        for (int i = numberOfOperands - 1; i >= 0; --i) {
            if (aptg.getParameterClass(i) != 32) {
                return false;
            }
        }
        return true;
    }
    
    private byte transformClass(final byte currentOperandClass, final byte desiredOperandClass, final boolean callerForceArrayFlag) {
        switch (desiredOperandClass) {
            case 32: {
                if (!callerForceArrayFlag) {
                    return 32;
                }
                return 64;
            }
            case 64: {
                return 64;
            }
            case 0: {
                if (!callerForceArrayFlag) {
                    return currentOperandClass;
                }
                return 0;
            }
            default: {
                throw new IllegalStateException("Unexpected operand class (" + desiredOperandClass + ")");
            }
        }
    }
    
    private void transformFunctionNode(final AbstractFunctionPtg afp, final ParseNode[] children, final byte desiredOperandClass, final boolean callerForceArrayFlag) {
        final byte defaultReturnOperandClass = afp.getDefaultOperandClass();
        boolean localForceArrayFlag = false;
        if (callerForceArrayFlag) {
            switch (defaultReturnOperandClass) {
                case 0: {
                    if (desiredOperandClass == 0) {
                        afp.setClass((byte)0);
                    }
                    else {
                        afp.setClass((byte)64);
                    }
                    localForceArrayFlag = false;
                    break;
                }
                case 64: {
                    afp.setClass((byte)64);
                    localForceArrayFlag = false;
                    break;
                }
                case 32: {
                    afp.setClass((byte)64);
                    localForceArrayFlag = true;
                    break;
                }
                default: {
                    throw new IllegalStateException("Unexpected operand class (" + defaultReturnOperandClass + ")");
                }
            }
        }
        else if (defaultReturnOperandClass == desiredOperandClass) {
            localForceArrayFlag = false;
            afp.setClass(defaultReturnOperandClass);
        }
        else {
            switch (desiredOperandClass) {
                case 32: {
                    afp.setClass((byte)32);
                    localForceArrayFlag = false;
                    break;
                }
                case 64: {
                    switch (defaultReturnOperandClass) {
                        case 0: {
                            afp.setClass((byte)0);
                            break;
                        }
                        case 32: {
                            afp.setClass((byte)64);
                            break;
                        }
                        default: {
                            throw new IllegalStateException("Unexpected operand class (" + defaultReturnOperandClass + ")");
                        }
                    }
                    localForceArrayFlag = (defaultReturnOperandClass == 32);
                    break;
                }
                case 0: {
                    switch (defaultReturnOperandClass) {
                        case 64: {
                            afp.setClass((byte)64);
                            break;
                        }
                        case 32: {
                            afp.setClass((byte)32);
                            break;
                        }
                        default: {
                            throw new IllegalStateException("Unexpected operand class (" + defaultReturnOperandClass + ")");
                        }
                    }
                    localForceArrayFlag = false;
                    break;
                }
                default: {
                    throw new IllegalStateException("Unexpected operand class (" + desiredOperandClass + ")");
                }
            }
        }
        for (int i = 0; i < children.length; ++i) {
            final ParseNode child = children[i];
            final byte paramOperandClass = afp.getParameterClass(i);
            this.transformNode(child, paramOperandClass, localForceArrayFlag);
        }
    }
    
    private void setSimpleValueFuncClass(final AbstractFunctionPtg afp, final byte desiredOperandClass, final boolean callerForceArrayFlag) {
        if (callerForceArrayFlag || desiredOperandClass == 64) {
            afp.setClass((byte)64);
        }
        else {
            afp.setClass((byte)32);
        }
    }
}
