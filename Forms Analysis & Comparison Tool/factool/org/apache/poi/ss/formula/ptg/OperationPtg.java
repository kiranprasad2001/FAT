// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.ptg;

public abstract class OperationPtg extends Ptg
{
    public static final int TYPE_UNARY = 0;
    public static final int TYPE_BINARY = 1;
    public static final int TYPE_FUNCTION = 2;
    
    public abstract String toFormulaString(final String[] p0);
    
    public abstract int getNumberOfOperands();
    
    @Override
    public byte getDefaultOperandClass() {
        return 32;
    }
}
