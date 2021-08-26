// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.ptg;

import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianInput;

public final class ExpPtg extends ControlPtg
{
    private static final int SIZE = 5;
    public static final short sid = 1;
    private final int field_1_first_row;
    private final int field_2_first_col;
    
    public ExpPtg(final LittleEndianInput in) {
        this.field_1_first_row = in.readShort();
        this.field_2_first_col = in.readShort();
    }
    
    public ExpPtg(final int firstRow, final int firstCol) {
        this.field_1_first_row = firstRow;
        this.field_2_first_col = firstCol;
    }
    
    @Override
    public void write(final LittleEndianOutput out) {
        out.writeByte(1 + this.getPtgClass());
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
        throw new RuntimeException("Coding Error: Expected ExpPtg to be converted from Shared to Non-Shared Formula by ValueRecordsAggregate, but it wasn't");
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer("[Array Formula or Shared Formula]\n");
        buffer.append("row = ").append(this.getRow()).append("\n");
        buffer.append("col = ").append(this.getColumn()).append("\n");
        return buffer.toString();
    }
}
