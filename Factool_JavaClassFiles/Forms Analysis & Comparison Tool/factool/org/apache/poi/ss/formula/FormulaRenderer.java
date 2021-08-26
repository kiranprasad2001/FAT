// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula;

import org.apache.poi.ss.formula.ptg.OperationPtg;
import org.apache.poi.ss.formula.ptg.AttrPtg;
import org.apache.poi.ss.formula.ptg.ParenthesisPtg;
import org.apache.poi.ss.formula.ptg.MemErrPtg;
import org.apache.poi.ss.formula.ptg.MemFuncPtg;
import org.apache.poi.ss.formula.ptg.MemAreaPtg;
import java.util.Stack;
import org.apache.poi.ss.formula.ptg.Ptg;

public class FormulaRenderer
{
    public static String toFormulaString(final FormulaRenderingWorkbook book, final Ptg[] ptgs) {
        if (ptgs == null || ptgs.length == 0) {
            throw new IllegalArgumentException("ptgs must not be null");
        }
        final Stack<String> stack = new Stack<String>();
        for (final Ptg ptg : ptgs) {
            if (!(ptg instanceof MemAreaPtg) && !(ptg instanceof MemFuncPtg)) {
                if (!(ptg instanceof MemErrPtg)) {
                    if (ptg instanceof ParenthesisPtg) {
                        final String contents = stack.pop();
                        stack.push("(" + contents + ")");
                    }
                    else if (ptg instanceof AttrPtg) {
                        final AttrPtg attrPtg = (AttrPtg)ptg;
                        if (!attrPtg.isOptimizedIf() && !attrPtg.isOptimizedChoose()) {
                            if (!attrPtg.isSkip()) {
                                if (!attrPtg.isSpace()) {
                                    if (!attrPtg.isSemiVolatile()) {
                                        if (!attrPtg.isSum()) {
                                            throw new RuntimeException("Unexpected tAttr: " + attrPtg.toString());
                                        }
                                        final String[] operands = getOperands(stack, attrPtg.getNumberOfOperands());
                                        stack.push(attrPtg.toFormulaString(operands));
                                    }
                                }
                            }
                        }
                    }
                    else if (ptg instanceof WorkbookDependentFormula) {
                        final WorkbookDependentFormula optg = (WorkbookDependentFormula)ptg;
                        stack.push(optg.toFormulaString(book));
                    }
                    else if (!(ptg instanceof OperationPtg)) {
                        stack.push(ptg.toFormulaString());
                    }
                    else {
                        final OperationPtg o = (OperationPtg)ptg;
                        final String[] operands = getOperands(stack, o.getNumberOfOperands());
                        stack.push(o.toFormulaString(operands));
                    }
                }
            }
        }
        if (stack.isEmpty()) {
            throw new IllegalStateException("Stack underflow");
        }
        final String result = stack.pop();
        if (!stack.isEmpty()) {
            throw new IllegalStateException("too much stuff left on the stack");
        }
        return result;
    }
    
    private static String[] getOperands(final Stack<String> stack, final int nOperands) {
        final String[] operands = new String[nOperands];
        for (int j = nOperands - 1; j >= 0; --j) {
            if (stack.isEmpty()) {
                final String msg = "Too few arguments supplied to operation. Expected (" + nOperands + ") operands but got (" + (nOperands - j - 1) + ")";
                throw new IllegalStateException(msg);
            }
            operands[j] = stack.pop();
        }
        return operands;
    }
}
