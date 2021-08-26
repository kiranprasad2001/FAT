// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.util;

import org.apache.poi.hssf.record.RecordInputStream;

public class CellRangeAddressList extends org.apache.poi.ss.util.CellRangeAddressList
{
    public CellRangeAddressList(final int firstRow, final int lastRow, final int firstCol, final int lastCol) {
        super(firstRow, lastRow, firstCol, lastCol);
    }
    
    public CellRangeAddressList() {
    }
    
    public CellRangeAddressList(final RecordInputStream in) {
        for (int nItems = in.readUShort(), k = 0; k < nItems; ++k) {
            this._list.add(new CellRangeAddress(in));
        }
    }
}
