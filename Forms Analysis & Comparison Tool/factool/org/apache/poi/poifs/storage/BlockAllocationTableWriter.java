// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.storage;

import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.poi.poifs.common.POIFSBigBlockSize;
import org.apache.poi.util.IntList;
import org.apache.poi.poifs.filesystem.BATManaged;

public final class BlockAllocationTableWriter implements BlockWritable, BATManaged
{
    private IntList _entries;
    private BATBlock[] _blocks;
    private int _start_block;
    private POIFSBigBlockSize _bigBlockSize;
    
    public BlockAllocationTableWriter(final POIFSBigBlockSize bigBlockSize) {
        this._bigBlockSize = bigBlockSize;
        this._start_block = -2;
        this._entries = new IntList();
        this._blocks = new BATBlock[0];
    }
    
    public int createBlocks() {
        int xbat_blocks = 0;
        int bat_blocks = 0;
        while (true) {
            final int calculated_bat_blocks = BATBlock.calculateStorageRequirements(this._bigBlockSize, bat_blocks + xbat_blocks + this._entries.size());
            final int calculated_xbat_blocks = HeaderBlockWriter.calculateXBATStorageRequirements(this._bigBlockSize, calculated_bat_blocks);
            if (bat_blocks == calculated_bat_blocks && xbat_blocks == calculated_xbat_blocks) {
                break;
            }
            bat_blocks = calculated_bat_blocks;
            xbat_blocks = calculated_xbat_blocks;
        }
        final int startBlock = this.allocateSpace(bat_blocks);
        this.allocateSpace(xbat_blocks);
        this.simpleCreateBlocks();
        return startBlock;
    }
    
    public int allocateSpace(final int blockCount) {
        final int startBlock = this._entries.size();
        if (blockCount > 0) {
            final int limit = blockCount - 1;
            int index = startBlock + 1;
            for (int k = 0; k < limit; ++k) {
                this._entries.add(index++);
            }
            this._entries.add(-2);
        }
        return startBlock;
    }
    
    public int getStartBlock() {
        return this._start_block;
    }
    
    void simpleCreateBlocks() {
        this._blocks = BATBlock.createBATBlocks(this._bigBlockSize, this._entries.toArray());
    }
    
    @Override
    public void writeBlocks(final OutputStream stream) throws IOException {
        for (int j = 0; j < this._blocks.length; ++j) {
            this._blocks[j].writeBlocks(stream);
        }
    }
    
    public static void writeBlock(final BATBlock bat, final ByteBuffer block) throws IOException {
        bat.writeData(block);
    }
    
    @Override
    public int countBlocks() {
        return this._blocks.length;
    }
    
    @Override
    public void setStartBlock(final int start_block) {
        this._start_block = start_block;
    }
}
