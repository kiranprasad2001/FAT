// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.ptg;

import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.BitField;

public abstract class RefPtgBase extends OperandPtg
{
    private int field_1_row;
    private int field_2_col;
    private static final BitField rowRelative;
    private static final BitField colRelative;
    private static final BitField column;
    
    protected RefPtgBase() {
    }
    
    protected RefPtgBase(final CellReference c) {
        this.setRow(c.getRow());
        this.setColumn(c.getCol());
        this.setColRelative(!c.isColAbsolute());
        this.setRowRelative(!c.isRowAbsolute());
    }
    
    protected final void readCoordinates(final LittleEndianInput in) {
        this.field_1_row = in.readUShort();
        this.field_2_col = in.readUShort();
    }
    
    protected final void writeCoordinates(final LittleEndianOutput out) {
        out.writeShort(this.field_1_row);
        out.writeShort(this.field_2_col);
    }
    
    public final void setRow(final int rowIndex) {
        this.field_1_row = rowIndex;
    }
    
    public final int getRow() {
        return this.field_1_row;
    }
    
    public final boolean isRowRelative() {
        return RefPtgBase.rowRelative.isSet(this.field_2_col);
    }
    
    public final void setRowRelative(final boolean rel) {
        this.field_2_col = RefPtgBase.rowRelative.setBoolean(this.field_2_col, rel);
    }
    
    public final boolean isColRelative() {
        return RefPtgBase.colRelative.isSet(this.field_2_col);
    }
    
    public final void setColRelative(final boolean rel) {
        this.field_2_col = RefPtgBase.colRelative.setBoolean(this.field_2_col, rel);
    }
    
    public final void setColumn(final int col) {
        this.field_2_col = RefPtgBase.column.setValue(this.field_2_col, col);
    }
    
    public final int getColumn() {
        return RefPtgBase.column.getValue(this.field_2_col);
    }
    
    protected final String formatReferenceAsString() {
        final CellReference cr = new CellReference(this.getRow(), this.getColumn(), !this.isRowRelative(), !this.isColRelative());
        return cr.formatAsString();
    }
    
    @Override
    public final byte getDefaultOperandClass() {
        return 0;
    }
    
    static {
        rowRelative = BitFieldFactory.getInstance(32768);
        colRelative = BitFieldFactory.getInstance(16384);
        column = BitFieldFactory.getInstance(16383);
    }
}
