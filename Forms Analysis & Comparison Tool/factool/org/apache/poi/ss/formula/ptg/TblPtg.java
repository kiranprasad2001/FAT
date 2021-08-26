// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.ptg;

import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianInput;

public final class TblPtg extends ControlPtg
{
    private static final int SIZE = 5;
    public static final short sid = 2;
    private final int field_1_first_row;
    private final int field_2_first_col;
    
    public TblPtg(final LittleEndianInput in) {
        this.field_1_first_row = in.readUShort();
        this.field_2_first_col = in.readUShort();
    }
    
    @Override
    public void write(final LittleEndianOutput out) {
        out.writeByte(2 + this.getPtgClass());
        out.writeShort(this.field_1_first_row);
        out.writeShort(this.field_2_first_col);
    }
    
    @Override
    public int getSize() {
        return 5;
    }
    
    public int getRow() {
        return this.field_1_first_row;
    }
    
    public int getColumn() {
        return this.field_2_first_col;
    }
    
    @Override
    public String toFormulaString() {
        throw new RuntimeException("Table and Arrays are not yet supported");
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer("[Data Table - Parent cell is an interior cell in a data table]\n");
        buffer.append("top left row = ").append(this.getRow()).append("\n");
        buffer.append("top left col = ").append(this.getColumn()).append("\n");
        return buffer.toString();
    }
}
