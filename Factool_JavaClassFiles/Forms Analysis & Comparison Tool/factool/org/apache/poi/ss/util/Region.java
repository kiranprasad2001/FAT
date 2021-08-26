// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.util;

public class Region implements Comparable<Region>
{
    private int _rowFrom;
    private short _colFrom;
    private int _rowTo;
    private short _colTo;
    
    public Region() {
    }
    
    public Region(final int rowFrom, final short colFrom, final int rowTo, final short colTo) {
        this._rowFrom = rowFrom;
        this._rowTo = rowTo;
        this._colFrom = colFrom;
        this._colTo = colTo;
    }
    
    public Region(final String ref) {
        final CellReference cellReferenceFrom = new CellReference(ref.substring(0, ref.indexOf(":")));
        final CellReference cellReferenceTo = new CellReference(ref.substring(ref.indexOf(":") + 1));
        this._rowFrom = cellReferenceFrom.getRow();
        this._colFrom = cellReferenceFrom.getCol();
        this._rowTo = cellReferenceTo.getRow();
        this._colTo = cellReferenceTo.getCol();
    }
    
    public short getColumnFrom() {
        return this._colFrom;
    }
    
    public int getRowFrom() {
        return this._rowFrom;
    }
    
    public short getColumnTo() {
        return this._colTo;
    }
    
    public int getRowTo() {
        return this._rowTo;
    }
    
    public void setColumnFrom(final short colFrom) {
        this._colFrom = colFrom;
    }
    
    public void setRowFrom(final int rowFrom) {
        this._rowFrom = rowFrom;
    }
    
    public void setColumnTo(final short colTo) {
        this._colTo = colTo;
    }
    
    public void setRowTo(final int rowTo) {
        this._rowTo = rowTo;
    }
    
    public boolean contains(final int row, final short col) {
        return this._rowFrom <= row && this._rowTo >= row && this._colFrom <= col && this._colTo >= col;
    }
    
    public boolean equals(final Region r) {
        return this.compareTo(r) == 0;
    }
    
    @Override
    public int hashCode() {
        assert false : "hashCode not designed";
        return 42;
    }
    
    @Override
    public int compareTo(final Region r) {
        if (this.getRowFrom() == r.getRowFrom() && this.getColumnFrom() == r.getColumnFrom() && this.getRowTo() == r.getRowTo() && this.getColumnTo() == r.getColumnTo()) {
            return 0;
        }
        if (this.getRowFrom() < r.getRowFrom() || this.getColumnFrom() < r.getColumnFrom() || this.getRowTo() < r.getRowTo() || this.getColumnTo() < r.getColumnTo()) {
            return 1;
        }
        return -1;
    }
    
    public int getArea() {
        return (this._rowTo - this._rowFrom + 1) * (this._colTo - this._colFrom + 1);
    }
    
    public static Region[] convertCellRangesToRegions(final CellRangeAddress[] cellRanges) {
        final int size = cellRanges.length;
        if (size < 1) {
            return new Region[0];
        }
        final Region[] result = new Region[size];
        for (int i = 0; i != size; ++i) {
            result[i] = convertToRegion(cellRanges[i]);
        }
        return result;
    }
    
    private static Region convertToRegion(final CellRangeAddress cr) {
        return new Region(cr.getFirstRow(), (short)cr.getFirstColumn(), cr.getLastRow(), (short)cr.getLastColumn());
    }
    
    public static CellRangeAddress[] convertRegionsToCellRanges(final Region[] regions) {
        final int size = regions.length;
        if (size < 1) {
            return new CellRangeAddress[0];
        }
        final CellRangeAddress[] result = new CellRangeAddress[size];
        for (int i = 0; i != size; ++i) {
            result[i] = convertToCellRangeAddress(regions[i]);
        }
        return result;
    }
    
    public static CellRangeAddress convertToCellRangeAddress(final Region r) {
        return new CellRangeAddress(r.getRowFrom(), r.getRowTo(), r.getColumnFrom(), r.getColumnTo());
    }
    
    public String getRegionRef() {
        final CellReference cellRefFrom = new CellReference(this._rowFrom, this._colFrom);
        final CellReference cellRefTo = new CellReference(this._rowTo, this._colTo);
        final String ref = cellRefFrom.formatAsString() + ":" + cellRefTo.formatAsString();
        return ref;
    }
}
