// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.ptg;

public final class NotEqualPtg extends ValueOperatorPtg
{
    public static final byte sid = 14;
    public static final ValueOperatorPtg instance;
    
    private NotEqualPtg() {
    }
    
    @Override
    protected byte getSid() {
        return 14;
    }
    
    @Override
    public int getNumberOfOperands() {
        return 2;
    }
    
    @Override
    public String toFormulaString(final String[] operands) {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(operands[0]);
        buffer.append("<>");
        buffer.append(operands[1]);
        return buffer.toString();
    }
    
    static {
        instance = new NotEqualPtg();
    }
}
