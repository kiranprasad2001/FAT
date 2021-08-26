// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.ptg;

import org.apache.poi.util.LittleEndianOutput;

public final class UnionPtg extends OperationPtg
{
    public static final byte sid = 16;
    public static final OperationPtg instance;
    
    private UnionPtg() {
    }
    
    @Override
    public final boolean isBaseToken() {
        return true;
    }
    
    @Override
    public int getSize() {
        return 1;
    }
    
    @Override
    public void write(final LittleEndianOutput out) {
        out.writeByte(16 + this.getPtgClass());
    }
    
    @Override
    public String toFormulaString() {
        return ",";
    }
    
    @Override
    public String toFormulaString(final String[] operands) {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(operands[0]);
        buffer.append(",");
        buffer.append(operands[1]);
        return buffer.toString();
    }
    
    @Override
    public int getNumberOfOperands() {
        return 2;
    }
    
    static {
        instance = new UnionPtg();
    }
}
