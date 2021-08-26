// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.ptg;

public final class PercentPtg extends ValueOperatorPtg
{
    public static final int SIZE = 1;
    public static final byte sid = 20;
    private static final String PERCENT = "%";
    public static final ValueOperatorPtg instance;
    
    private PercentPtg() {
    }
    
    @Override
    protected byte getSid() {
        return 20;
    }
    
    @Override
    public int getNumberOfOperands() {
        return 1;
    }
    
    @Override
    public String toFormulaString(final String[] operands) {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(operands[0]);
        buffer.append("%");
        return buffer.toString();
    }
    
    static {
        instance = new PercentPtg();
    }
}
