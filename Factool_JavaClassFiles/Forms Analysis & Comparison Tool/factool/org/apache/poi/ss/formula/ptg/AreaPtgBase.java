// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.ptg;

import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.util.BitField;

public abstract class AreaPtgBase extends OperandPtg implements AreaI
{
    private int field_1_first_row;
    private int field_2_last_row;
    private int field_3_first_column;
    private int field_4_last_column;
    private static final BitField rowRelative;
    private static final BitField colRelative;
    private static final BitField columnMask;
    
    protected final RuntimeException notImplemented() {
        return new RuntimeException("Coding Error: This method should never be called. This ptg should be converted");
    }
    
    protected AreaPtgBase() {
    }
    
    protected AreaPtgBase(final AreaReference ar) {
        final CellReference firstCell = ar.getFirstCell();
        final CellReference lastCell = ar.getLastCell();
        this.setFirstRow(firstCell.getRow());
        this.setFirstColumn((firstCell.getCol() == -1) ? 0 : firstCell.getCol());
        this.setLastRow(lastCell.getRow());
        this.setLastColumn((lastCell.getCol() == -1) ? 255 : lastCell.getCol());
        this.setFirstColRelative(!firstCell.isColAbsolute());
        this.setLastColRelative(!lastCell.isColAbsolute());
        this.setFirstRowRelative(!firstCell.isRowAbsolute());
        this.setLastRowRelative(!lastCell.isRowAbsolute());
    }
    
    protected AreaPtgBase(final int firstRow, final int lastRow, final int firstColumn, final int lastColumn, final boolean firstRowRelative, final boolean lastRowRelative, final boolean firstColRelative, final boolean lastColRelative) {
        if (lastRow >= firstRow) {
            this.setFirstRow(firstRow);
            this.setLastRow(lastRow);
            this.setFirstRowRelative(firstRowRelative);
            this.setLastRowRelative(lastRowRelative);
        }
        else {
            this.setFirstRow(lastRow);
            this.setLastRow(firstRow);
            this.setFirstRowRelative(lastRowRelative);
            this.setLastRowRelative(firstRowRelative);
        }
        if (lastColumn >= firstColumn) {
            this.setFirstColumn(firstColumn);
            this.setLastColumn(lastColumn);
            this.setFirstColRelative(firstColRelative);
            this.setLastColRelative(lastColRelative);
        }
        else {
            this.setFirstColumn(lastColumn);
            this.setLastColumn(firstColumn);
            this.setFirstColRelative(lastColRelative);
            this.setLastColRelative(firstColRelative);
        }
    }
    
    protected final void readCoordinates(final LittleEndianInput in) {
        this.field_1_first_row = in.readUShort();
        this.field_2_last_row = in.readUShort();
        this.field_3_first_column = in.readUShort();
        this.field_4_last_column = in.readUShort();
    }
    
    protected final void writeCoordinates(final LittleEndianOutput out) {
        out.writeShort(this.field_1_first_row);
        out.writeShort(this.field_2_last_row);
        out.writeShort(this.field_3_first_column);
        out.writeShort(this.field_4_last_column);
    }
    
    @Override
    public final int getFirstRow() {
        return this.field_1_first_row;
    }
    
    public final void setFirstRow(final int rowIx) {
        this.field_1_first_row = rowIx;
    }
    
    @Override
    public final int getLastRow() {
        return this.field_2_last_row;
    }
    
    public final void setLastRow(final int rowIx) {
        this.field_2_last_row = rowIx;
    }
    
    @Override
    public final int getFirstColumn() {
        return AreaPtgBase.columnMask.getValue(this.field_3_first_column);
    }
    
    public final short getFirstColumnRaw() {
        return (short)this.field_3_first_column;
    }
    
    public final boolean isFirstRowRelative() {
        return AreaPtgBase.rowRelative.isSet(this.field_3_first_column);
    }
    
    public final void setFirstRowRelative(final boolean rel) {
        this.field_3_first_column = AreaPtgBase.rowRelative.setBoolean(this.field_3_first_column, rel);
    }
    
    public final boolean isFirstColRelative() {
        return AreaPtgBase.colRelative.isSet(this.field_3_first_column);
    }
    
    public final void setFirstColRelative(final boolean rel) {
        this.field_3_first_column = AreaPtgBase.colRelative.setBoolean(this.field_3_first_column, rel);
    }
    
    public final void setFirstColumn(final int colIx) {
        this.field_3_first_column = AreaPtgBase.columnMask.setValue(this.field_3_first_column, colIx);
    }
    
    public final void setFirstColumnRaw(final int column) {
        this.field_3_first_column = column;
    }
    
    @Override
    public final int getLastColumn() {
        return AreaPtgBase.columnMask.getValue(this.field_4_last_column);
    }
    
    public final short getLastColumnRaw() {
        return (short)this.field_4_last_column;
    }
    
    public final boolean isLastRowRelative() {
        return AreaPtgBase.rowRelative.isSet(this.field_4_last_column);
    }
    
    public final void setLastRowRelative(final boolean rel) {
        this.field_4_last_column = AreaPtgBase.rowRelative.setBoolean(this.field_4_last_column, rel);
    }
    
    public final boolean isLastColRelative() {
        return AreaPtgBase.colRelative.isSet(this.field_4_last_column);
    }
    
    public final void setLastColRelative(final boolean rel) {
        this.field_4_last_column = AreaPtgBase.colRelative.setBoolean(this.field_4_last_column, rel);
    }
    
    public final void setLastColumn(final int colIx) {
        this.field_4_last_column = AreaPtgBase.columnMask.setValue(this.field_4_last_column, colIx);
    }
    
    public final void setLastColumnRaw(final short column) {
        this.field_4_last_column = column;
    }
    
    protected final String formatReferenceAsString() {
        final CellReference topLeft = new CellReference(this.getFirstRow(), this.getFirstColumn(), !this.isFirstRowRelative(), !this.isFirstColRelative());
        final CellReference botRight = new CellReference(this.getLastRow(), this.getLastColumn(), !this.isLastRowRelative(), !this.isLastColRelative());
        if (AreaReference.isWholeColumnReference(topLeft, botRight)) {
            return new AreaReference(topLeft, botRight).formatAsString();
        }
        return topLeft.formatAsString() + ":" + botRight.formatAsString();
    }
    
    @Override
    public String toFormulaString() {
        return this.formatReferenceAsString();
    }
    
    @Override
    public byte getDefaultOperandClass() {
        return 0;
    }
    
    static {
        rowRelative = BitFieldFactory.getInstance(32768);
        colRelative = BitFieldFactory.getInstance(16384);
        columnMask = BitFieldFactory.getInstance(16383);
    }
}
