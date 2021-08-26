// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.ptg;

import org.apache.poi.ss.formula.function.FunctionMetadata;
import org.apache.poi.ss.formula.function.FunctionMetadataRegistry;

public abstract class AbstractFunctionPtg extends OperationPtg
{
    public static final String FUNCTION_NAME_IF = "IF";
    private static final short FUNCTION_INDEX_EXTERNAL = 255;
    private final byte returnClass;
    private final byte[] paramClass;
    private final byte _numberOfArgs;
    private final short _functionIndex;
    
    protected AbstractFunctionPtg(final int functionIndex, final int pReturnClass, final byte[] paramTypes, final int nParams) {
        this._numberOfArgs = (byte)nParams;
        this._functionIndex = (short)functionIndex;
        this.returnClass = (byte)pReturnClass;
        this.paramClass = paramTypes;
    }
    
    @Override
    public final boolean isBaseToken() {
        return false;
    }
    
    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder(64);
        sb.append(this.getClass().getName()).append(" [");
        sb.append(this.lookupName(this._functionIndex));
        sb.append(" nArgs=").append(this._numberOfArgs);
        sb.append("]");
        return sb.toString();
    }
    
    public final short getFunctionIndex() {
        return this._functionIndex;
    }
    
    @Override
    public final int getNumberOfOperands() {
        return this._numberOfArgs;
    }
    
    public final String getName() {
        return this.lookupName(this._functionIndex);
    }
    
    public final boolean isExternalFunction() {
        return this._functionIndex == 255;
    }
    
    @Override
    public final String toFormulaString() {
        return this.getName();
    }
    
    @Override
    public String toFormulaString(final String[] operands) {
        final StringBuilder buf = new StringBuilder();
        if (this.isExternalFunction()) {
            buf.append(operands[0]);
            appendArgs(buf, 1, operands);
        }
        else {
            buf.append(this.getName());
            appendArgs(buf, 0, operands);
        }
        return buf.toString();
    }
    
    private static void appendArgs(final StringBuilder buf, final int firstArgIx, final String[] operands) {
        buf.append('(');
        for (int i = firstArgIx; i < operands.length; ++i) {
            if (i > firstArgIx) {
                buf.append(',');
            }
            buf.append(operands[i]);
        }
        buf.append(")");
    }
    
    @Override
    public abstract int getSize();
    
    public static final boolean isBuiltInFunctionName(final String name) {
        final short ix = FunctionMetadataRegistry.lookupIndexByName(name.toUpperCase());
        return ix >= 0;
    }
    
    protected final String lookupName(final short index) {
        if (index == 255) {
            return "#external#";
        }
        final FunctionMetadata fm = FunctionMetadataRegistry.getFunctionByIndex(index);
        if (fm == null) {
            throw new RuntimeException("bad function index (" + index + ")");
        }
        return fm.getName();
    }
    
    protected static short lookupIndex(final String name) {
        final short ix = FunctionMetadataRegistry.lookupIndexByName(name.toUpperCase());
        if (ix < 0) {
            return 255;
        }
        return ix;
    }
    
    @Override
    public byte getDefaultOperandClass() {
        return this.returnClass;
    }
    
    public final byte getParameterClass(final int index) {
        if (index >= this.paramClass.length) {
            return this.paramClass[this.paramClass.length - 1];
        }
        return this.paramClass[index];
    }
}
