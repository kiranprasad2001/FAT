// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.ptg;

import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.LittleEndianInput;

abstract class Ref2DPtgBase extends RefPtgBase
{
    private static final int SIZE = 5;
    
    protected Ref2DPtgBase(final int row, final int column, final boolean isRowRelative, final boolean isColumnRelative) {
        this.setRow(row);
        this.setColumn(column);
        this.setRowRelative(isRowRelative);
        this.setColRelative(isColumnRelative);
    }
    
    protected Ref2DPtgBase(final LittleEndianInput in) {
        this.readCoordinates(in);
    }
    
    protected Ref2DPtgBase(final CellReference cr) {
        super(cr);
    }
    
    @Override
    public void write(final LittleEndianOutput out) {
        out.writeByte(this.getSid() + this.getPtgClass());
        this.writeCoordinates(out);
    }
    
    @Override
    public final String toFormulaString() {
        return this.formatReferenceAsString();
    }
    
    protected abstract byte getSid();
    
    @Override
    public final int getSize() {
        return 5;
    }
    
    @Override
    public final String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(this.getClass().getName());
        sb.append(" [");
        sb.append(this.formatReferenceAsString());
        sb.append("]");
        return sb.toString();
    }
}
