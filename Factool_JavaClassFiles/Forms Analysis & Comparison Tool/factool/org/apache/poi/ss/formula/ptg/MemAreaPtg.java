// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.ptg;

import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianInput;

public final class MemAreaPtg extends OperandPtg
{
    public static final short sid = 38;
    private static final int SIZE = 7;
    private final int field_1_reserved;
    private final int field_2_subex_len;
    
    public MemAreaPtg(final int subexLen) {
        this.field_1_reserved = 0;
        this.field_2_subex_len = subexLen;
    }
    
    public MemAreaPtg(final LittleEndianInput in) {
        this.field_1_reserved = in.readInt();
        this.field_2_subex_len = in.readShort();
    }
    
    public int getLenRefSubexpression() {
        return this.field_2_subex_len;
    }
    
    @Override
    public void write(final LittleEndianOutput out) {
        out.writeByte(38 + this.getPtgClass());
        out.writeInt(this.field_1_reserved);
        out.writeShort(this.field_2_subex_len);
    }
    
    @Override
    public int getSize() {
        return 7;
    }
    
    @Override
    public String toFormulaString() {
        return "";
    }
    
    @Override
    public byte getDefaultOperandClass() {
        return 32;
    }
    
    @Override
    public final String toString() {
        final StringBuffer sb = new StringBuffer(64);
        sb.append(this.getClass().getName()).append(" [len=");
        sb.append(this.field_2_subex_len);
        sb.append("]");
        return sb.toString();
    }
}
