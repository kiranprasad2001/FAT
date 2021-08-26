// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.util;

import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianByteArrayOutputStream;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.ss.util.CellRangeAddressBase;

public final class CellRangeAddress8Bit extends CellRangeAddressBase
{
    public static final int ENCODED_SIZE = 6;
    
    public CellRangeAddress8Bit(final int firstRow, final int lastRow, final int firstCol, final int lastCol) {
        super(firstRow, lastRow, firstCol, lastCol);
    }
    
    public CellRangeAddress8Bit(final LittleEndianInput in) {
        super(readUShortAndCheck(in), in.readUShort(), in.readUByte(), in.readUByte());
    }
    
    private static int readUShortAndCheck(final LittleEndianInput in) {
        if (in.available() < 6) {
            throw new RuntimeException("Ran out of data reading CellRangeAddress");
        }
        return in.readUShort();
    }
    
    @Deprecated
    public int serialize(final int offset, final byte[] data) {
        this.serialize(new LittleEndianByteArrayOutputStream(data, offset, 6));
        return 6;
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this.getFirstRow());
        out.writeShort(this.getLastRow());
        out.writeByte(this.getFirstColumn());
        out.writeByte(this.getLastColumn());
    }
    
    public CellRangeAddress8Bit copy() {
        return new CellRangeAddress8Bit(this.getFirstRow(), this.getLastRow(), this.getFirstColumn(), this.getLastColumn());
    }
    
    public static int getEncodedSize(final int numberOfItems) {
        return numberOfItems * 6;
    }
}
