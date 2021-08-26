// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.ptg;

import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.util.LittleEndianInput;

public final class AreaPtg extends Area2DPtgBase
{
    public static final short sid = 37;
    
    public AreaPtg(final int firstRow, final int lastRow, final int firstColumn, final int lastColumn, final boolean firstRowRelative, final boolean lastRowRelative, final boolean firstColRelative, final boolean lastColRelative) {
        super(firstRow, lastRow, firstColumn, lastColumn, firstRowRelative, lastRowRelative, firstColRelative, lastColRelative);
    }
    
    public AreaPtg(final LittleEndianInput in) {
        super(in);
    }
    
    public AreaPtg(final String arearef) {
        super(new AreaReference(arearef));
    }
    
    public AreaPtg(final AreaReference areaRef) {
        super(areaRef);
    }
    
    @Override
    protected byte getSid() {
        return 37;
    }
}
