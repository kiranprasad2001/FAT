// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.util;

import org.apache.poi.hssf.record.RecordInputStream;

public class CellRangeAddress extends org.apache.poi.ss.util.CellRangeAddress
{
    public CellRangeAddress(final int firstRow, final int lastRow, final int firstCol, final int lastCol) {
        super(firstRow, lastRow, firstCol, lastCol);
    }
    
    public CellRangeAddress(final RecordInputStream in) {
        super(in);
    }
}
