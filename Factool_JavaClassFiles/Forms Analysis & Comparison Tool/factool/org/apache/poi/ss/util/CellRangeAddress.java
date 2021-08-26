// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.util;

import org.apache.poi.ss.formula.SheetNameFormatter;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianByteArrayOutputStream;

public class CellRangeAddress extends CellRangeAddressBase
{
    public static final int ENCODED_SIZE = 8;
    
    public CellRangeAddress(final int firstRow, final int lastRow, final int firstCol, final int lastCol) {
        super(firstRow, lastRow, firstCol, lastCol);
        if (lastRow < firstRow || lastCol < firstCol) {
            throw new IllegalArgumentException("lastRow < firstRow || lastCol < firstCol");
        }
    }
    
    @Deprecated
    public int serialize(final int offset, final byte[] data) {
        this.serialize(new LittleEndianByteArrayOutputStream(data, offset, 8));
        return 8;
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.getFirstRow());
        out.writeShort(this.getLastRow());
        out.writeShort(this.getFirstColumn());
        out.writeShort(this.getLastColumn());
    }
    
    public CellRangeAddress(final RecordInputStream in) {
        super(readUShortAndCheck(in), in.readUShort(), in.readUShort(), in.readUShort());
    }
    
    private static int readUShortAndCheck(final RecordInputStream in) {
        if (in.remaining() < 8) {
            throw new RuntimeException("Ran out of data reading CellRangeAddress");
        }
        return in.readUShort();
    }
    
    public CellRangeAddress copy() {
        return new CellRangeAddress(this.getFirstRow(), this.getLastRow(), this.getFirstColumn(), this.getLastColumn());
    }
    
    public static int getEncodedSize(final int numberOfItems) {
        return numberOfItems * 8;
    }
    
    public String formatAsString() {
        return this.formatAsString(null, false);
    }
    
    public String formatAsString(final String sheetName, final boolean useAbsoluteAddress) {
        final StringBuffer sb = new StringBuffer();
        if (sheetName != null) {
            sb.append(SheetNameFormatter.format(sheetName));
            sb.append("!");
        }
        final CellReference cellRefFrom = new CellReference(this.getFirstRow(), this.getFirstColumn(), useAbsoluteAddress, useAbsoluteAddress);
        final CellReference cellRefTo = new CellReference(this.getLastRow(), this.getLastColumn(), useAbsoluteAddress, useAbsoluteAddress);
        sb.append(cellRefFrom.formatAsString());
        if (!cellRefFrom.equals(cellRefTo) || this.isFullColumnRange() || this.isFullRowRange()) {
            sb.append(':');
            sb.append(cellRefTo.formatAsString());
        }
        return sb.toString();
    }
    
    public static CellRangeAddress valueOf(final String ref) {
        final int sep = ref.indexOf(":");
        CellReference b;
        CellReference a;
        if (sep == -1) {
            a = (b = new CellReference(ref));
        }
        else {
            a = new CellReference(ref.substring(0, sep));
            b = new CellReference(ref.substring(sep + 1));
        }
        return new CellRangeAddress(a.getRow(), b.getRow(), a.getCol(), b.getCol());
    }
}
