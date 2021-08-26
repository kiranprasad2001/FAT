// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.ptg;

import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.ss.util.CellReference;

public final class RefPtg extends Ref2DPtgBase
{
    public static final byte sid = 36;
    
    public RefPtg(final String cellref) {
        super(new CellReference(cellref));
    }
    
    public RefPtg(final int row, final int column, final boolean isRowRelative, final boolean isColumnRelative) {
        super(row, column, isRowRelative, isColumnRelative);
    }
    
    public RefPtg(final LittleEndianInput in) {
        super(in);
    }
    
    public RefPtg(final CellReference cr) {
        super(cr);
    }
    
    @Override
    protected byte getSid() {
        return 36;
    }
}
