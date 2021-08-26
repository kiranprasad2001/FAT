// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.ptg;

public final class LessEqualPtg extends ValueOperatorPtg
{
    public static final byte sid = 10;
    public static final ValueOperatorPtg instance;
    
    private LessEqualPtg() {
    }
    
    @Override
    protected byte getSid() {
        return 10;
    }
    
    @Override
    public int getNumberOfOperands() {
        return 2;
    }
    
    @Override
    public String toFormulaString(final String[] operands) {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(operands[0]);
        buffer.append("<=");
        buffer.append(operands[1]);
        return buffer.toString();
    }
    
    static {
        instance = new LessEqualPtg();
    }
}
