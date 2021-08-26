// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.storage;

import java.io.OutputStream;
import java.util.Arrays;
import org.apache.poi.util.IOUtils;
import org.apache.poi.poifs.common.POIFSBigBlockSize;
import java.io.InputStream;
import java.io.IOException;
import org.apache.poi.poifs.common.POIFSConstants;

public final class DocumentBlock extends BigBlock
{
    private static final byte _default_value = -1;
    private byte[] _data;
    private int _bytes_read;
    
    public DocumentBlock(final RawDataBlock block) throws IOException {
        super((block.getBigBlockSize() == 512) ? POIFSConstants.SMALLER_BIG_BLOCK_SIZE_DETAILS : POIFSConstants.LARGER_BIG_BLOCK_SIZE_DETAILS);
        this._data = block.getData();
        this._bytes_read = this._data.length;
    }
    
    public DocumentBlock(final InputStream stream, final POIFSBigBlockSize bigBlockSize) throws IOException {
        this(bigBlockSize);
        final int count = IOUtils.readFully(stream, this._data);
        this._bytes_read = ((count == -1) ? 0 : count);
    }
    
    private DocumentBlock(final POIFSBigBlockSize bigBlockSize) {
        super(bigBlockSize);
        Arrays.fill(this._data = new byte[bigBlockSize.getBigBlockSize()], (byte)(-1));
    }
    
    public int size() {
        return this._bytes_read;
    }
    
    public boolean partiallyRead() {
        return this._bytes_read != this.bigBlockSize.getBigBlockSize();
    }
    
    public static byte getFillByte() {
        return -1;
    }
    
    public static DocumentBlock[] convert(final POIFSBigBlockSize bigBlockSize, final byte[] array, final int size) {
        final DocumentBlock[] rval = new DocumentBlock[(size + bigBlockSize.getBigBlockSize() - 1) / bigBlockSize.getBigBlockSize()];
        int offset = 0;
        for (int k = 0; k < rval.length; ++k) {
            rval[k] = new DocumentBlock(bigBlockSize);
            if (offset < array.length) {
                final int length = Math.min(bigBlockSize.getBigBlockSize(), array.length - offset);
                System.arraycopy(array, offset, rval[k]._data, 0, length);
                if (length != bigBlockSize.getBigBlockSize()) {
                    Arrays.fill(rval[k]._data, length, bigBlockSize.getBigBlockSize(), (byte)(-1));
                }
            }
            else {
                Arrays.fill(rval[k]._data, (byte)(-1));
            }
            offset += bigBlockSize.getBigBlockSize();
        }
        return rval;
    }
    
    public static DataInputBlock getDataInputBlock(final DocumentBlock[] blocks, final int offset) {
        if (blocks == null || blocks.length == 0) {
            return null;
        }
        final POIFSBigBlockSize bigBlockSize = blocks[0].bigBlockSize;
        final int BLOCK_SHIFT = bigBlockSize.getHeaderValue();
        final int BLOCK_SIZE = bigBlockSize.getBigBlockSize();
        final int BLOCK_MASK = BLOCK_SIZE - 1;
        final int firstBlockIndex = offset >> BLOCK_SHIFT;
        final int firstBlockOffset = offset & BLOCK_MASK;
        return new DataInputBlock(blocks[firstBlockIndex]._data, firstBlockOffset);
    }
    
    @Override
    void writeData(final OutputStream stream) throws IOException {
        this.doWriteData(stream, this._data);
    }
}
