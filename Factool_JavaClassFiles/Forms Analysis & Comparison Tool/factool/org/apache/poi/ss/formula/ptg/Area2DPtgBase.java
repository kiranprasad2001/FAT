// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.ptg;

import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.ss.util.AreaReference;

public abstract class Area2DPtgBase extends AreaPtgBase
{
    private static final int SIZE = 9;
    
    protected Area2DPtgBase(final int firstRow, final int lastRow, final int firstColumn, final int lastColumn, final boolean firstRowRelative, final boolean lastRowRelative, final boolean firstColRelative, final boolean lastColRelative) {
        super(firstRow, lastRow, firstColumn, lastColumn, firstRowRelative, lastRowRelative, firstColRelative, lastColRelative);
    }
    
    protected Area2DPtgBase(final AreaReference ar) {
        super(ar);
    }
    
    protected Area2DPtgBase(final LittleEndianInput in) {
        this.readCoordinates(in);
    }
    
    protected abstract byte getSid();
    
    @Override
    public final void write(final LittleEndianOutput out) {
        out.writeByte(this.getSid() + this.getPtgClass());
        this.writeCoordinates(out);
    }
    
    @Override
    public final int getSize() {
        return 9;
    }
    
    @Override
    public final String toFormulaString() {
        return this.formatReferenceAsString();
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
