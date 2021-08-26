// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.filesystem;

import org.apache.poi.poifs.storage.BlockAllocationTableWriter;
import java.io.IOException;
import java.util.Iterator;
import java.nio.ByteBuffer;
import org.apache.poi.poifs.property.RootProperty;
import org.apache.poi.poifs.storage.HeaderBlock;
import org.apache.poi.poifs.storage.BATBlock;
import java.util.List;

public class NPOIFSMiniStore extends BlockStore
{
    private NPOIFSFileSystem _filesystem;
    private NPOIFSStream _mini_stream;
    private List<BATBlock> _sbat_blocks;
    private HeaderBlock _header;
    private RootProperty _root;
    
    protected NPOIFSMiniStore(final NPOIFSFileSystem filesystem, final RootProperty root, final List<BATBlock> sbats, final HeaderBlock header) {
        this._filesystem = filesystem;
        this._sbat_blocks = sbats;
        this._header = header;
        this._root = root;
        this._mini_stream = new NPOIFSStream(filesystem, root.getStartBlock());
    }
    
    @Override
    protected ByteBuffer getBlockAt(final int offset) throws IOException {
        final int byteOffset = offset * 64;
        final int bigBlockNumber = byteOffset / this._filesystem.getBigBlockSize();
        final int bigBlockOffset = byteOffset % this._filesystem.getBigBlockSize();
        final Iterator<ByteBuffer> it = this._mini_stream.getBlockIterator();
        for (int i = 0; i < bigBlockNumber; ++i) {
            it.next();
        }
        final ByteBuffer dataBlock = it.next();
        if (dataBlock == null) {
            throw new IndexOutOfBoundsException("Big block " + bigBlockNumber + " outside stream");
        }
        dataBlock.position(dataBlock.position() + bigBlockOffset);
        final ByteBuffer miniBuffer = dataBlock.slice();
        miniBuffer.limit(64);
        return miniBuffer;
    }
    
    @Override
    protected ByteBuffer createBlockIfNeeded(final int offset) throws IOException {
        boolean firstInStore = false;
        if (this._mini_stream.getStartBlock() == -2) {
            firstInStore = true;
        }
        if (!firstInStore) {
            try {
                return this.getBlockAt(offset);
            }
            catch (IndexOutOfBoundsException ex) {}
        }
        final int newBigBlock = this._filesystem.getFreeBlock();
        this._filesystem.createBlockIfNeeded(newBigBlock);
        if (firstInStore) {
            this._filesystem._get_property_table().getRoot().setStartBlock(newBigBlock);
            this._mini_stream = new NPOIFSStream(this._filesystem, newBigBlock);
        }
        else {
            final ChainLoopDetector loopDetector = this._filesystem.getChainLoopDetector();
            int block = this._mini_stream.getStartBlock();
            while (true) {
                loopDetector.claim(block);
                final int next = this._filesystem.getNextBlock(block);
                if (next == -2) {
                    break;
                }
                block = next;
            }
            this._filesystem.setNextBlock(block, newBigBlock);
        }
        this._filesystem.setNextBlock(newBigBlock, -2);
        return this.createBlockIfNeeded(offset);
    }
    
    @Override
    protected BATBlock.BATBlockAndIndex getBATBlockAndIndex(final int offset) {
        return BATBlock.getSBATBlockAndIndex(offset, this._header, this._sbat_blocks);
    }
    
    @Override
    protected int getNextBlock(final int offset) {
        final BATBlock.BATBlockAndIndex bai = this.getBATBlockAndIndex(offset);
        return bai.getBlock().getValueAt(bai.getIndex());
    }
    
    @Override
    protected void setNextBlock(final int offset, final int nextBlock) {
        final BATBlock.BATBlockAndIndex bai = this.getBATBlockAndIndex(offset);
        bai.getBlock().setValueAt(bai.getIndex(), nextBlock);
    }
    
    @Override
    protected int getFreeBlock() throws IOException {
        final int sectorsPerSBAT = this._filesystem.getBigBlockSizeDetails().getBATEntriesPerBlock();
        int offset = 0;
        for (int i = 0; i < this._sbat_blocks.size(); ++i) {
            final BATBlock sbat = this._sbat_blocks.get(i);
            if (sbat.hasFreeSectors()) {
                for (int j = 0; j < sectorsPerSBAT; ++j) {
                    final int sbatValue = sbat.getValueAt(j);
                    if (sbatValue == -1) {
                        return offset + j;
                    }
                }
            }
            offset += sectorsPerSBAT;
        }
        final BATBlock newSBAT = BATBlock.createEmptyBATBlock(this._filesystem.getBigBlockSizeDetails(), false);
        final int batForSBAT = this._filesystem.getFreeBlock();
        newSBAT.setOurBlockIndex(batForSBAT);
        if (this._header.getSBATCount() == 0) {
            this._header.setSBATStart(batForSBAT);
            this._header.setSBATBlockCount(1);
        }
        else {
            final ChainLoopDetector loopDetector = this._filesystem.getChainLoopDetector();
            int batOffset = this._header.getSBATStart();
            while (true) {
                loopDetector.claim(batOffset);
                final int nextBat = this._filesystem.getNextBlock(batOffset);
                if (nextBat == -2) {
                    break;
                }
                batOffset = nextBat;
            }
            this._filesystem.setNextBlock(batOffset, batForSBAT);
            this._header.setSBATBlockCount(this._header.getSBATCount() + 1);
        }
        this._filesystem.setNextBlock(batForSBAT, -2);
        this._sbat_blocks.add(newSBAT);
        return offset;
    }
    
    @Override
    protected ChainLoopDetector getChainLoopDetector() throws IOException {
        return new ChainLoopDetector(this._root.getSize());
    }
    
    @Override
    protected int getBlockStoreBlockSize() {
        return 64;
    }
    
    protected void syncWithDataSource() throws IOException {
        for (final BATBlock sbat : this._sbat_blocks) {
            final ByteBuffer block = this._filesystem.getBlockAt(sbat.getOurBlockIndex());
            BlockAllocationTableWriter.writeBlock(sbat, block);
        }
    }
}
