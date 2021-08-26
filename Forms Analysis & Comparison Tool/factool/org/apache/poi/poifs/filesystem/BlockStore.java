// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.filesystem;

import org.apache.poi.poifs.storage.BATBlock;
import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class BlockStore
{
    protected abstract int getBlockStoreBlockSize();
    
    protected abstract ByteBuffer getBlockAt(final int p0) throws IOException;
    
    protected abstract ByteBuffer createBlockIfNeeded(final int p0) throws IOException;
    
    protected abstract BATBlock.BATBlockAndIndex getBATBlockAndIndex(final int p0);
    
    protected abstract int getNextBlock(final int p0);
    
    protected abstract void setNextBlock(final int p0, final int p1);
    
    protected abstract int getFreeBlock() throws IOException;
    
    protected abstract ChainLoopDetector getChainLoopDetector() throws IOException;
    
    protected class ChainLoopDetector
    {
        private boolean[] used_blocks;
        
        protected ChainLoopDetector(final long rawSize) {
            final int numBlocks = (int)Math.ceil((double)(rawSize / BlockStore.this.getBlockStoreBlockSize()));
            this.used_blocks = new boolean[numBlocks];
        }
        
        protected void claim(final int offset) {
            if (offset >= this.used_blocks.length) {
                return;
            }
            if (this.used_blocks[offset]) {
                throw new IllegalStateException("Potential loop detected - Block " + offset + " was already claimed but was just requested again");
            }
            this.used_blocks[offset] = true;
        }
    }
}
