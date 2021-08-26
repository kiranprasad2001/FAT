// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.storage;

import org.apache.poi.util.POILogFactory;
import java.io.OutputStream;
import org.apache.poi.util.HexDump;
import org.apache.poi.util.ShortField;
import org.apache.poi.util.LongField;
import java.util.Arrays;
import org.apache.poi.poifs.filesystem.NotOLE2FileException;
import org.apache.poi.hssf.OldExcelFormatException;
import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.apache.poi.util.IntegerField;
import org.apache.poi.poifs.common.POIFSConstants;
import org.apache.poi.util.LittleEndian;
import java.nio.ByteBuffer;
import java.io.IOException;
import org.apache.poi.util.IOUtils;
import java.io.InputStream;
import org.apache.poi.poifs.common.POIFSBigBlockSize;
import org.apache.poi.util.POILogger;

public final class HeaderBlock implements HeaderBlockConstants
{
    private static final POILogger _logger;
    private final POIFSBigBlockSize bigBlockSize;
    private int _bat_count;
    private int _property_start;
    private int _sbat_start;
    private int _sbat_count;
    private int _xbat_start;
    private int _xbat_count;
    private final byte[] _data;
    private static final byte _default_value = -1;
    
    public HeaderBlock(final InputStream stream) throws IOException {
        this(readFirst512(stream));
        if (this.bigBlockSize.getBigBlockSize() != 512) {
            final int rest = this.bigBlockSize.getBigBlockSize() - 512;
            final byte[] tmp = new byte[rest];
            IOUtils.readFully(stream, tmp);
        }
    }
    
    public HeaderBlock(final ByteBuffer buffer) throws IOException {
        this(IOUtils.toByteArray(buffer, 512));
    }
    
    private HeaderBlock(final byte[] data) throws IOException {
        this._data = data;
        final long signature = LittleEndian.getLong(this._data, 0);
        if (signature == -2226271756974174256L) {
            if (this._data[30] == 12) {
                this.bigBlockSize = POIFSConstants.LARGER_BIG_BLOCK_SIZE_DETAILS;
            }
            else {
                if (this._data[30] != 9) {
                    throw new IOException("Unsupported blocksize  (2^" + this._data[30] + "). Expected 2^9 or 2^12.");
                }
                this.bigBlockSize = POIFSConstants.SMALLER_BIG_BLOCK_SIZE_DETAILS;
            }
            this._bat_count = new IntegerField(44, data).get();
            this._property_start = new IntegerField(48, this._data).get();
            this._sbat_start = new IntegerField(60, this._data).get();
            this._sbat_count = new IntegerField(64, this._data).get();
            this._xbat_start = new IntegerField(68, this._data).get();
            this._xbat_count = new IntegerField(72, this._data).get();
            return;
        }
        final byte[] OOXML_FILE_HEADER = POIFSConstants.OOXML_FILE_HEADER;
        if (this._data[0] == OOXML_FILE_HEADER[0] && this._data[1] == OOXML_FILE_HEADER[1] && this._data[2] == OOXML_FILE_HEADER[2] && this._data[3] == OOXML_FILE_HEADER[3]) {
            throw new OfficeXmlFileException("The supplied data appears to be in the Office 2007+ XML. You are calling the part of POI that deals with OLE2 Office Documents. You need to call a different part of POI to process this data (eg XSSF instead of HSSF)");
        }
        if (this._data[0] == 9 && this._data[1] == 0 && this._data[2] == 4 && this._data[3] == 0 && this._data[4] == 0 && this._data[5] == 0 && (this._data[6] == 16 || this._data[6] == 32 || this._data[6] == 64) && this._data[7] == 0) {
            throw new OldExcelFormatException("The supplied data appears to be in BIFF2 format. HSSF only supports the BIFF8 format, try OldExcelExtractor");
        }
        if (this._data[0] == 9 && this._data[1] == 2 && this._data[2] == 6 && this._data[3] == 0 && this._data[4] == 0 && this._data[5] == 0 && (this._data[6] == 16 || this._data[6] == 32 || this._data[6] == 64) && this._data[7] == 0) {
            throw new OldExcelFormatException("The supplied data appears to be in BIFF3 format. HSSF only supports the BIFF8 format, try OldExcelExtractor");
        }
        if (this._data[0] == 9 && this._data[1] == 4 && this._data[2] == 6 && this._data[3] == 0 && this._data[4] == 0 && this._data[5] == 0 && (((this._data[6] == 16 || this._data[6] == 32 || this._data[6] == 64) && this._data[7] == 0) || (this._data[6] == 0 && this._data[7] == 1))) {
            throw new OldExcelFormatException("The supplied data appears to be in BIFF4 format. HSSF only supports the BIFF8 format, try OldExcelExtractor");
        }
        throw new NotOLE2FileException("Invalid header signature; read " + longToHex(signature) + ", expected " + longToHex(-2226271756974174256L) + " - Your file appears " + "not to be a valid OLE2 document");
    }
    
    public HeaderBlock(final POIFSBigBlockSize bigBlockSize) {
        this.bigBlockSize = bigBlockSize;
        Arrays.fill(this._data = new byte[512], (byte)(-1));
        new LongField(0, -2226271756974174256L, this._data);
        new IntegerField(8, 0, this._data);
        new IntegerField(12, 0, this._data);
        new IntegerField(16, 0, this._data);
        new IntegerField(20, 0, this._data);
        new ShortField(24, (short)59, this._data);
        new ShortField(26, (short)3, this._data);
        new ShortField(28, (short)(-2), this._data);
        new ShortField(30, bigBlockSize.getHeaderValue(), this._data);
        new IntegerField(32, 6, this._data);
        new IntegerField(36, 0, this._data);
        new IntegerField(40, 0, this._data);
        new IntegerField(52, 0, this._data);
        new IntegerField(56, 4096, this._data);
        this._bat_count = 0;
        this._sbat_count = 0;
        this._xbat_count = 0;
        this._property_start = -2;
        this._sbat_start = -2;
        this._xbat_start = -2;
    }
    
    private static byte[] readFirst512(final InputStream stream) throws IOException {
        final byte[] data = new byte[512];
        final int bsCount = IOUtils.readFully(stream, data);
        if (bsCount != 512) {
            throw alertShortRead(bsCount, 512);
        }
        return data;
    }
    
    private static String longToHex(final long value) {
        return new String(HexDump.longToHex(value));
    }
    
    private static IOException alertShortRead(final int pRead, final int expectedReadSize) {
        int read;
        if (pRead < 0) {
            read = 0;
        }
        else {
            read = pRead;
        }
        final String type = " byte" + ((read == 1) ? "" : "s");
        return new IOException("Unable to read entire header; " + read + type + " read; expected " + expectedReadSize + " bytes");
    }
    
    public int getPropertyStart() {
        return this._property_start;
    }
    
    public void setPropertyStart(final int startBlock) {
        this._property_start = startBlock;
    }
    
    public int getSBATStart() {
        return this._sbat_start;
    }
    
    public int getSBATCount() {
        return this._sbat_count;
    }
    
    public void setSBATStart(final int startBlock) {
        this._sbat_start = startBlock;
    }
    
    public void setSBATBlockCount(final int count) {
        this._sbat_count = count;
    }
    
    public int getBATCount() {
        return this._bat_count;
    }
    
    public void setBATCount(final int count) {
        this._bat_count = count;
    }
    
    public int[] getBATArray() {
        final int[] result = new int[Math.min(this._bat_count, 109)];
        int offset = 76;
        for (int j = 0; j < result.length; ++j) {
            result[j] = LittleEndian.getInt(this._data, offset);
            offset += 4;
        }
        return result;
    }
    
    public void setBATArray(final int[] bat_array) {
        final int count = Math.min(bat_array.length, 109);
        final int blank = 109 - count;
        int offset = 76;
        for (int i = 0; i < count; ++i) {
            LittleEndian.putInt(this._data, offset, bat_array[i]);
            offset += 4;
        }
        for (int i = 0; i < blank; ++i) {
            LittleEndian.putInt(this._data, offset, -1);
            offset += 4;
        }
    }
    
    public int getXBATCount() {
        return this._xbat_count;
    }
    
    public void setXBATCount(final int count) {
        this._xbat_count = count;
    }
    
    public int getXBATIndex() {
        return this._xbat_start;
    }
    
    public void setXBATStart(final int startBlock) {
        this._xbat_start = startBlock;
    }
    
    public POIFSBigBlockSize getBigBlockSize() {
        return this.bigBlockSize;
    }
    
    void writeData(final OutputStream stream) throws IOException {
        new IntegerField(44, this._bat_count, this._data);
        new IntegerField(48, this._property_start, this._data);
        new IntegerField(60, this._sbat_start, this._data);
        new IntegerField(64, this._sbat_count, this._data);
        new IntegerField(68, this._xbat_start, this._data);
        new IntegerField(72, this._xbat_count, this._data);
        stream.write(this._data, 0, 512);
        for (int i = 512; i < this.bigBlockSize.getBigBlockSize(); ++i) {
            stream.write(0);
        }
    }
    
    static {
        _logger = POILogFactory.getLogger(HeaderBlock.class);
    }
}
