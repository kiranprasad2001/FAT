// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.storage;

import java.util.ArrayList;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Arrays;
import org.apache.poi.poifs.common.POIFSBigBlockSize;

public final class SmallDocumentBlock implements BlockWritable, ListManagedBlock
{
    private static final int BLOCK_SHIFT = 6;
    private byte[] _data;
    private static final byte _default_fill = -1;
    private static final int _block_size = 64;
    private static final int BLOCK_MASK = 63;
    private final int _blocks_per_big_block;
    private final POIFSBigBlockSize _bigBlockSize;
    
    private SmallDocumentBlock(final POIFSBigBlockSize bigBlockSize, final byte[] data, final int index) {
        this(bigBlockSize);
        System.arraycopy(data, index * 64, this._data, 0, 64);
    }
    
    private SmallDocumentBlock(final POIFSBigBlockSize bigBlockSize) {
        this._bigBlockSize = bigBlockSize;
        this._blocks_per_big_block = getBlocksPerBigBlock(bigBlockSize);
        this._data = new byte[64];
    }
    
    private static int getBlocksPerBigBlock(final POIFSBigBlockSize bigBlockSize) {
        return bigBlockSize.getBigBlockSize() / 64;
    }
    
    public static SmallDocumentBlock[] convert(final POIFSBigBlockSize bigBlockSize, final byte[] array, final int size) {
        final SmallDocumentBlock[] rval = new SmallDocumentBlock[(size + 64 - 1) / 64];
        int offset = 0;
        for (int k = 0; k < rval.length; ++k) {
            rval[k] = new SmallDocumentBlock(bigBlockSize);
            if (offset < array.length) {
                final int length = Math.min(64, array.length - offset);
                System.arraycopy(array, offset, rval[k]._data, 0, length);
                if (length != 64) {
                    Arrays.fill(rval[k]._data, length, 64, (byte)(-1));
                }
            }
            else {
                Arrays.fill(rval[k]._data, (byte)(-1));
            }
            offset += 64;
        }
        return rval;
    }
    
    public static int fill(final POIFSBigBlockSize bigBlockSize, final List blocks) {
        final int _blocks_per_big_block = getBlocksPerBigBlock(bigBlockSize);
        int count = blocks.size();
        final int big_block_count = (count + _blocks_per_big_block - 1) / _blocks_per_big_block;
        for (int full_count = big_block_count * _blocks_per_big_block; count < full_count; ++count) {
            blocks.add(makeEmptySmallDocumentBlock(bigBlockSize));
        }
        return big_block_count;
    }
    
    public static SmallDocumentBlock[] convert(final POIFSBigBlockSize bigBlockSize, final BlockWritable[] store, final int size) throws IOException, ArrayIndexOutOfBoundsException {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        for (int j = 0; j < store.length; ++j) {
            store[j].writeBlocks(stream);
        }
        final byte[] data = stream.toByteArray();
        final SmallDocumentBlock[] rval = new SmallDocumentBlock[convertToBlockCount(size)];
        for (int index = 0; index < rval.length; ++index) {
            rval[index] = new SmallDocumentBlock(bigBlockSize, data, index);
        }
        return rval;
    }
    
    public static List extract(final POIFSBigBlockSize bigBlockSize, final ListManagedBlock[] blocks) throws IOException {
        final int _blocks_per_big_block = getBlocksPerBigBlock(bigBlockSize);
        final List sdbs = new ArrayList();
        for (int j = 0; j < blocks.length; ++j) {
            final byte[] data = blocks[j].getData();
            for (int k = 0; k < _blocks_per_big_block; ++k) {
                sdbs.add(new SmallDocumentBlock(bigBlockSize, data, k));
            }
        }
        return sdbs;
    }
    
    public static DataInputBlock getDataInputBlock(final SmallDocumentBlock[] blocks, final int offset) {
        final int firstBlockIndex = offset >> 6;
        final int firstBlockOffset = offset & 0x3F;
        return new DataInputBlock(blocks[firstBlockIndex]._data, firstBlockOffset);
    }
    
    public static int calcSize(final int size) {
        return size * 64;
    }
    
    private static SmallDocumentBlock makeEmptySmallDocumentBlock(final POIFSBigBlockSize bigBlockSize) {
        final SmallDocumentBlock block = new SmallDocumentBlock(bigBlockSize);
        Arrays.fill(block._data, (byte)(-1));
        return block;
    }
    
    private static int convertToBlockCount(final int size) {
        return (size + 64 - 1) / 64;
    }
    
    @Override
    public void writeBlocks(final OutputStream stream) throws IOException {
        stream.write(this._data);
    }
    
    @Override
    public byte[] getData() {
        return this._data;
    }
    
    public POIFSBigBlockSize getBigBlockSize() {
        return this._bigBlockSize;
    }
}
