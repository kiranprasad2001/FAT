// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.ptg;

import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianInput;

public final class MemErrPtg extends OperandPtg
{
    public static final short sid = 39;
    private static final int SIZE = 7;
    private int field_1_reserved;
    private short field_2_subex_len;
    
    public MemErrPtg(final LittleEndianInput in) {
        this.field_1_reserved = in.readInt();
        this.field_2_subex_len = in.readShort();
    }
    
    @Override
    public void write(final LittleEndianOutput out) {
        out.writeByte(39 + this.getPtgClass());
        out.writeInt(this.field_1_reserved);
        out.writeShort(this.field_2_subex_len);
    }
    
    @Override
    public int getSize() {
        return 7;
    }
    
    @Override
    public String toFormulaString() {
        return "ERR#";
    }
    
    @Override
    public byte getDefaultOperandClass() {
        return 32;
    }
}
