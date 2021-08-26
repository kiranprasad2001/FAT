// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.util;

import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianByteArrayOutputStream;
import org.apache.poi.hssf.record.RecordInputStream;
import java.util.ArrayList;
import java.util.List;

public class CellRangeAddressList
{
    protected final List<CellRangeAddress> _list;
    
    public CellRangeAddressList() {
        this._list = new ArrayList<CellRangeAddress>();
    }
    
    public CellRangeAddressList(final int firstRow, final int lastRow, final int firstCol, final int lastCol) {
        this();
        this.addCellRangeAddress(firstRow, firstCol, lastRow, lastCol);
    }
    
    public CellRangeAddressList(final RecordInputStream in) {
        this();
        for (int nItems = in.readUShort(), k = 0; k < nItems; ++k) {
            this._list.add(new CellRangeAddress(in));
        }
    }
    
    public int countRanges() {
        return this._list.size();
    }
    
    public void addCellRangeAddress(final int firstRow, final int firstCol, final int lastRow, final int lastCol) {
        final CellRangeAddress region = new CellRangeAddress(firstRow, lastRow, firstCol, lastCol);
        this.addCellRangeAddress(region);
    }
    
    public void addCellRangeAddress(final CellRangeAddress cra) {
        this._list.add(cra);
    }
    
    public CellRangeAddress remove(final int rangeIndex) {
        if (this._list.isEmpty()) {
            throw new RuntimeException("List is empty");
        }
        if (rangeIndex < 0 || rangeIndex >= this._list.size()) {
            throw new RuntimeException("Range index (" + rangeIndex + ") is outside allowable range (0.." + (this._list.size() - 1) + ")");
        }
        return this._list.remove(rangeIndex);
    }
    
    public CellRangeAddress getCellRangeAddress(final int index) {
        return this._list.get(index);
    }
    
    public int getSize() {
        return getEncodedSize(this._list.size());
    }
    
    public static int getEncodedSize(final int numberOfRanges) {
        return 2 + CellRangeAddress.getEncodedSize(numberOfRanges);
    }
    
    public int serialize(final int offset, final byte[] data) {
        final int totalSize = this.getSize();
        this.serialize(new LittleEndianByteArrayOutputStream(data, offset, totalSize));
        return totalSize;
    }
    
    public void serialize(final LittleEndianOutput out) {
        final int nItems = this._list.size();
        out.writeShort(nItems);
        for (int k = 0; k < nItems; ++k) {
            final CellRangeAddress region = this._list.get(k);
            region.serialize(out);
        }
    }
    
    public CellRangeAddressList copy() {
        final CellRangeAddressList result = new CellRangeAddressList();
        for (int nItems = this._list.size(), k = 0; k < nItems; ++k) {
            final CellRangeAddress region = this._list.get(k);
            result.addCellRangeAddress(region.copy());
        }
        return result;
    }
    
    public CellRangeAddress[] getCellRangeAddresses() {
        final CellRangeAddress[] result = new CellRangeAddress[this._list.size()];
        this._list.toArray(result);
        return result;
    }
}
