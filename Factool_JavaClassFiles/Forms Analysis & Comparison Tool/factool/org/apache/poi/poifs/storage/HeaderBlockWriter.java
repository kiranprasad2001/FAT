// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.storage;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.poi.poifs.common.POIFSBigBlockSize;

public class HeaderBlockWriter implements HeaderBlockConstants, BlockWritable
{
    private final HeaderBlock _header_block;
    
    public HeaderBlockWriter(final POIFSBigBlockSize bigBlockSize) {
        this._header_block = new HeaderBlock(bigBlockSize);
    }
    
    public HeaderBlockWriter(final HeaderBlock headerBlock) {
        this._header_block = headerBlock;
    }
    
    public BATBlock[] setBATBlocks(final int blockCount, final int startBlock) {
        final POIFSBigBlockSize bigBlockSize = this._header_block.getBigBlockSize();
        this._header_block.setBATCount(blockCount);
        final int limit = Math.min(blockCount, 109);
        final int[] bat_blocks = new int[limit];
        for (int j = 0; j < limit; ++j) {
            bat_blocks[j] = startBlock + j;
        }
        this._header_block.setBATArray(bat_blocks);
        BATBlock[] rvalue;
        if (blockCount > 109) {
            final int excess_blocks = blockCount - 109;
            final int[] excess_block_array = new int[excess_blocks];
            for (int i = 0; i < excess_blocks; ++i) {
                excess_block_array[i] = startBlock + i + 109;
            }
            rvalue = BATBlock.createXBATBlocks(bigBlockSize, excess_block_array, startBlock + blockCount);
            this._header_block.setXBATStart(startBlock + blockCount);
        }
        else {
            rvalue = BATBlock.createXBATBlocks(bigBlockSize, new int[0], 0);
            this._header_block.setXBATStart(-2);
        }
        this._header_block.setXBATCount(rvalue.length);
        return rvalue;
    }
    
    public void setPropertyStart(final int startBlock) {
        this._header_block.setPropertyStart(startBlock);
    }
    
    public void setSBATStart(final int startBlock) {
        this._header_block.setSBATStart(startBlock);
    }
    
    public void setSBATBlockCount(final int count) {
        this._header_block.setSBATBlockCount(count);
    }
    
    static int calculateXBATStorageRequirements(final POIFSBigBlockSize bigBlockSize, final int blockCount) {
        return (blockCount > 109) ? BATBlock.calculateXBATStorageRequirements(bigBlockSize, blockCount - 109) : 0;
    }
    
    @Override
    public void writeBlocks(final OutputStream stream) throws IOException {
        this._header_block.writeData(stream);
    }
    
    public void writeBlock(final ByteBuffer block) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(this._header_block.getBigBlockSize().getBigBlockSize());
        this._header_block.writeData(baos);
        block.put(baos.toByteArray());
    }
}
