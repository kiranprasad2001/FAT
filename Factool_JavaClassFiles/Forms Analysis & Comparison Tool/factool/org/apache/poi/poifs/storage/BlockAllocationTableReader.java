// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.storage;

import org.apache.poi.util.POILogFactory;
import java.util.List;
import java.util.ArrayList;
import org.apache.poi.util.LittleEndian;
import java.io.IOException;
import org.apache.poi.poifs.common.POIFSBigBlockSize;
import org.apache.poi.util.IntList;
import org.apache.poi.util.POILogger;

public final class BlockAllocationTableReader
{
    private static final POILogger _logger;
    private static final int MAX_BLOCK_COUNT = 65535;
    private final IntList _entries;
    private POIFSBigBlockSize bigBlockSize;
    
    public BlockAllocationTableReader(final POIFSBigBlockSize bigBlockSize, final int block_count, final int[] block_array, final int xbat_count, final int xbat_index, final BlockList raw_block_list) throws IOException {
        this(bigBlockSize);
        sanityCheckBlockCount(block_count);
        int limit = Math.min(block_count, block_array.length);
        final RawDataBlock[] blocks = new RawDataBlock[block_count];
        int block_index;
        for (block_index = 0; block_index < limit; ++block_index) {
            final int nextOffset = block_array[block_index];
            if (nextOffset > raw_block_list.blockCount()) {
                throw new IOException("Your file contains " + raw_block_list.blockCount() + " sectors, but the initial DIFAT array at index " + block_index + " referenced block # " + nextOffset + ". This isn't allowed and " + " your file is corrupt");
            }
            blocks[block_index] = (RawDataBlock)raw_block_list.remove(nextOffset);
        }
        if (block_index < block_count) {
            if (xbat_index < 0) {
                throw new IOException("BAT count exceeds limit, yet XBAT index indicates no valid entries");
            }
            int chain_index = xbat_index;
            final int max_entries_per_block = bigBlockSize.getXBATEntriesPerBlock();
            final int chain_index_offset = bigBlockSize.getNextXBATChainOffset();
            for (int j = 0; j < xbat_count; ++j) {
                limit = Math.min(block_count - block_index, max_entries_per_block);
                final byte[] data = raw_block_list.remove(chain_index).getData();
                int offset = 0;
                for (int k = 0; k < limit; ++k) {
                    blocks[block_index++] = (RawDataBlock)raw_block_list.remove(LittleEndian.getInt(data, offset));
                    offset += 4;
                }
                chain_index = LittleEndian.getInt(data, chain_index_offset);
                if (chain_index == -2) {
                    break;
                }
            }
        }
        if (block_index != block_count) {
            throw new IOException("Could not find all blocks");
        }
        this.setEntries(blocks, raw_block_list);
    }
    
    BlockAllocationTableReader(final POIFSBigBlockSize bigBlockSize, final ListManagedBlock[] blocks, final BlockList raw_block_list) throws IOException {
        this(bigBlockSize);
        this.setEntries(blocks, raw_block_list);
    }
    
    BlockAllocationTableReader(final POIFSBigBlockSize bigBlockSize) {
        this.bigBlockSize = bigBlockSize;
        this._entries = new IntList();
    }
    
    public static void sanityCheckBlockCount(final int block_count) throws IOException {
        if (block_count <= 0) {
            throw new IOException("Illegal block count; minimum count is 1, got " + block_count + " instead");
        }
        if (block_count > 65535) {
            throw new IOException("Block count " + block_count + " is too high. POI maximum is " + 65535 + ".");
        }
    }
    
    ListManagedBlock[] fetchBlocks(final int startBlock, final int headerPropertiesStartBlock, final BlockList blockList) throws IOException {
        final List<ListManagedBlock> blocks = new ArrayList<ListManagedBlock>();
        int currentBlock = startBlock;
        boolean firstPass = true;
        ListManagedBlock dataBlock = null;
        while (currentBlock != -2) {
            try {
                dataBlock = blockList.remove(currentBlock);
                blocks.add(dataBlock);
                currentBlock = this._entries.get(currentBlock);
                firstPass = false;
            }
            catch (IOException e) {
                if (currentBlock == headerPropertiesStartBlock) {
                    BlockAllocationTableReader._logger.log(5, "Warning, header block comes after data blocks in POIFS block listing");
                    currentBlock = -2;
                }
                else {
                    if (currentBlock != 0 || !firstPass) {
                        throw e;
                    }
                    BlockAllocationTableReader._logger.log(5, "Warning, incorrectly terminated empty data blocks in POIFS block listing (should end at -2, ended at 0)");
                    currentBlock = -2;
                }
            }
        }
        return blocks.toArray(new ListManagedBlock[blocks.size()]);
    }
    
    boolean isUsed(final int index) {
        try {
            return this._entries.get(index) != -1;
        }
        catch (IndexOutOfBoundsException e) {
            return false;
        }
    }
    
    int getNextBlockIndex(final int index) throws IOException {
        if (this.isUsed(index)) {
            return this._entries.get(index);
        }
        throw new IOException("index " + index + " is unused");
    }
    
    private void setEntries(final ListManagedBlock[] blocks, final BlockList raw_blocks) throws IOException {
        final int limit = this.bigBlockSize.getBATEntriesPerBlock();
        for (int block_index = 0; block_index < blocks.length; ++block_index) {
            final byte[] data = blocks[block_index].getData();
            int offset = 0;
            for (int k = 0; k < limit; ++k) {
                final int entry = LittleEndian.getInt(data, offset);
                if (entry == -1) {
                    raw_blocks.zap(this._entries.size());
                }
                this._entries.add(entry);
                offset += 4;
            }
            blocks[block_index] = null;
        }
        raw_blocks.setBAT(this);
    }
    
    static {
        _logger = POILogFactory.getLogger(BlockAllocationTableReader.class);
    }
}
