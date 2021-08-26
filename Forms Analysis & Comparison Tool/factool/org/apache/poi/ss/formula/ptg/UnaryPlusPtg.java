// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.ptg;

public final class UnaryPlusPtg extends ValueOperatorPtg
{
    public static final byte sid = 18;
    private static final String ADD = "+";
    public static final ValueOperatorPtg instance;
    
    private UnaryPlusPtg() {
    }
    
    @Override
    protected byte getSid() {
        return 18;
    }
    
    @Override
    public int getNumberOfOperands() {
        return 1;
    }
    
    @Override
    public String toFormulaString(final String[] operands) {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("+");
        buffer.append(operands[0]);
        return buffer.toString();
    }
    
    static {
        instance = new UnaryPlusPtg();
    }
}
