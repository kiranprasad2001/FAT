// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.storage;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import org.apache.poi.util.LittleEndian;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.apache.poi.poifs.common.POIFSBigBlockSize;

public final class BATBlock extends BigBlock
{
    private int[] _values;
    private boolean _has_free_sectors;
    private int ourBlockIndex;
    
    private BATBlock(final POIFSBigBlockSize bigBlockSize) {
        super(bigBlockSize);
        final int _entries_per_block = bigBlockSize.getBATEntriesPerBlock();
        this._values = new int[_entries_per_block];
        this._has_free_sectors = true;
        Arrays.fill(this._values, -1);
    }
    
    private BATBlock(final POIFSBigBlockSize bigBlockSize, final int[] entries, final int start_index, final int end_index) {
        this(bigBlockSize);
        for (int k = start_index; k < end_index; ++k) {
            this._values[k - start_index] = entries[k];
        }
        if (end_index - start_index == this._values.length) {
            this.recomputeFree();
        }
    }
    
    private void recomputeFree() {
        boolean hasFree = false;
        for (int k = 0; k < this._values.length; ++k) {
            if (this._values[k] == -1) {
                hasFree = true;
                break;
            }
        }
        this._has_free_sectors = hasFree;
    }
    
    public static BATBlock createBATBlock(final POIFSBigBlockSize bigBlockSize, final ByteBuffer data) {
        final BATBlock block = new BATBlock(bigBlockSize);
        final byte[] buffer = new byte[4];
        for (int i = 0; i < block._values.length; ++i) {
            data.get(buffer);
            block._values[i] = LittleEndian.getInt(buffer);
        }
        block.recomputeFree();
        return block;
    }
    
    public static BATBlock createEmptyBATBlock(final POIFSBigBlockSize bigBlockSize, final boolean isXBAT) {
        final BATBlock block = new BATBlock(bigBlockSize);
        if (isXBAT) {
            block.setXBATChain(bigBlockSize, -2);
        }
        return block;
    }
    
    public static BATBlock[] createBATBlocks(final POIFSBigBlockSize bigBlockSize, final int[] entries) {
        final int block_count = calculateStorageRequirements(bigBlockSize, entries.length);
        final BATBlock[] blocks = new BATBlock[block_count];
        int index = 0;
        int remaining = entries.length;
        for (int _entries_per_block = bigBlockSize.getBATEntriesPerBlock(), j = 0; j < entries.length; j += _entries_per_block) {
            blocks[index++] = new BATBlock(bigBlockSize, entries, j, (remaining > _entries_per_block) ? (j + _entries_per_block) : entries.length);
            remaining -= _entries_per_block;
        }
        return blocks;
    }
    
    public static BATBlock[] createXBATBlocks(final POIFSBigBlockSize bigBlockSize, final int[] entries, final int startBlock) {
        final int block_count = calculateXBATStorageRequirements(bigBlockSize, entries.length);
        final BATBlock[] blocks = new BATBlock[block_count];
        int index = 0;
        int remaining = entries.length;
        final int _entries_per_xbat_block = bigBlockSize.getXBATEntriesPerBlock();
        if (block_count != 0) {
            for (int j = 0; j < entries.length; j += _entries_per_xbat_block) {
                blocks[index++] = new BATBlock(bigBlockSize, entries, j, (remaining > _entries_per_xbat_block) ? (j + _entries_per_xbat_block) : entries.length);
                remaining -= _entries_per_xbat_block;
            }
            for (index = 0; index < blocks.length - 1; ++index) {
                blocks[index].setXBATChain(bigBlockSize, startBlock + index + 1);
            }
            blocks[index].setXBATChain(bigBlockSize, -2);
        }
        return blocks;
    }
    
    public static int calculateStorageRequirements(final POIFSBigBlockSize bigBlockSize, final int entryCount) {
        final int _entries_per_block = bigBlockSize.getBATEntriesPerBlock();
        return (entryCount + _entries_per_block - 1) / _entries_per_block;
    }
    
    public static int calculateXBATStorageRequirements(final POIFSBigBlockSize bigBlockSize, final int entryCount) {
        final int _entries_per_xbat_block = bigBlockSize.getXBATEntriesPerBlock();
        return (entryCount + _entries_per_xbat_block - 1) / _entries_per_xbat_block;
    }
    
    public static long calculateMaximumSize(final POIFSBigBlockSize bigBlockSize, final int numBATs) {
        long size = 1L;
        size += numBATs * bigBlockSize.getBATEntriesPerBlock();
        return size * bigBlockSize.getBigBlockSize();
    }
    
    public static long calculateMaximumSize(final HeaderBlock header) {
        return calculateMaximumSize(header.getBigBlockSize(), header.getBATCount());
    }
    
    public static BATBlockAndIndex getBATBlockAndIndex(final int offset, final HeaderBlock header, final List<BATBlock> bats) {
        final POIFSBigBlockSize bigBlockSize = header.getBigBlockSize();
        final int whichBAT = (int)Math.floor(offset / bigBlockSize.getBATEntriesPerBlock());
        final int index = offset % bigBlockSize.getBATEntriesPerBlock();
        return new BATBlockAndIndex(index, (BATBlock)bats.get(whichBAT));
    }
    
    public static BATBlockAndIndex getSBATBlockAndIndex(final int offset, final HeaderBlock header, final List<BATBlock> sbats) {
        final POIFSBigBlockSize bigBlockSize = header.getBigBlockSize();
        final int whichSBAT = (int)Math.floor(offset / bigBlockSize.getBATEntriesPerBlock());
        final int index = offset % bigBlockSize.getBATEntriesPerBlock();
        return new BATBlockAndIndex(index, (BATBlock)sbats.get(whichSBAT));
    }
    
    private void setXBATChain(final POIFSBigBlockSize bigBlockSize, final int chainIndex) {
        final int _entries_per_xbat_block = bigBlockSize.getXBATEntriesPerBlock();
        this._values[_entries_per_xbat_block] = chainIndex;
    }
    
    public boolean hasFreeSectors() {
        return this._has_free_sectors;
    }
    
    public int getValueAt(final int relativeOffset) {
        if (relativeOffset >= this._values.length) {
            throw new ArrayIndexOutOfBoundsException("Unable to fetch offset " + relativeOffset + " as the " + "BAT only contains " + this._values.length + " entries");
        }
        return this._values[relativeOffset];
    }
    
    public void setValueAt(final int relativeOffset, final int value) {
        final int oldValue = this._values[relativeOffset];
        this._values[relativeOffset] = value;
        if (value == -1) {
            this._has_free_sectors = true;
            return;
        }
        if (oldValue == -1) {
            this.recomputeFree();
        }
    }
    
    public void setOurBlockIndex(final int index) {
        this.ourBlockIndex = index;
    }
    
    public int getOurBlockIndex() {
        return this.ourBlockIndex;
    }
    
    @Override
    void writeData(final OutputStream stream) throws IOException {
        stream.write(this.serialize());
    }
    
    void writeData(final ByteBuffer block) throws IOException {
        block.put(this.serialize());
    }
    
    private byte[] serialize() {
        final byte[] data = new byte[this.bigBlockSize.getBigBlockSize()];
        int offset = 0;
        for (int i = 0; i < this._values.length; ++i) {
            LittleEndian.putInt(data, offset, this._values[i]);
            offset += 4;
        }
        return data;
    }
    
    public static class BATBlockAndIndex
    {
        private final int index;
        private final BATBlock block;
        
        private BATBlockAndIndex(final int index, final BATBlock block) {
            this.index = index;
            this.block = block;
        }
        
        public int getIndex() {
            return this.index;
        }
        
        public BATBlock getBlock() {
            return this.block;
        }
    }
}
