// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.filesystem;

import java.io.IOException;
import java.util.Iterator;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class NPOIFSStream implements Iterable<ByteBuffer>
{
    private BlockStore blockStore;
    private int startBlock;
    private OutputStream outStream;
    
    public NPOIFSStream(final BlockStore blockStore, final int startBlock) {
        this.blockStore = blockStore;
        this.startBlock = startBlock;
    }
    
    public NPOIFSStream(final BlockStore blockStore) {
        this.blockStore = blockStore;
        this.startBlock = -2;
    }
    
    public int getStartBlock() {
        return this.startBlock;
    }
    
    @Override
    public Iterator<ByteBuffer> iterator() {
        return this.getBlockIterator();
    }
    
    public Iterator<ByteBuffer> getBlockIterator() {
        if (this.startBlock == -2) {
            throw new IllegalStateException("Can't read from a new stream before it has been written to");
        }
        return new StreamBlockByteBufferIterator(this.startBlock);
    }
    
    public void updateContents(final byte[] contents) throws IOException {
        final OutputStream os = this.getOutputStream();
        os.write(contents);
        os.close();
    }
    
    public OutputStream getOutputStream() throws IOException {
        if (this.outStream == null) {
            this.outStream = new StreamBlockByteBuffer();
        }
        return this.outStream;
    }
    
    public void free() throws IOException {
        final BlockStore.ChainLoopDetector loopDetector = this.blockStore.getChainLoopDetector();
        this.free(loopDetector);
    }
    
    private void free(final BlockStore.ChainLoopDetector loopDetector) {
        int nextBlock = this.startBlock;
        while (nextBlock != -2) {
            final int thisBlock = nextBlock;
            loopDetector.claim(thisBlock);
            nextBlock = this.blockStore.getNextBlock(thisBlock);
            this.blockStore.setNextBlock(thisBlock, -1);
        }
        this.startBlock = -2;
    }
    
    protected class StreamBlockByteBufferIterator implements Iterator<ByteBuffer>
    {
        private BlockStore.ChainLoopDetector loopDetector;
        private int nextBlock;
        
        protected StreamBlockByteBufferIterator(final int firstBlock) {
            this.nextBlock = firstBlock;
            try {
                this.loopDetector = NPOIFSStream.this.blockStore.getChainLoopDetector();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        
        @Override
        public boolean hasNext() {
            return this.nextBlock != -2;
        }
        
        @Override
        public ByteBuffer next() {
            if (this.nextBlock == -2) {
                throw new IndexOutOfBoundsException("Can't read past the end of the stream");
            }
            try {
                this.loopDetector.claim(this.nextBlock);
                final ByteBuffer data = NPOIFSStream.this.blockStore.getBlockAt(this.nextBlock);
                this.nextBlock = NPOIFSStream.this.blockStore.getNextBlock(this.nextBlock);
                return data;
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    protected class StreamBlockByteBuffer extends OutputStream
    {
        byte[] oneByte;
        ByteBuffer buffer;
        BlockStore.ChainLoopDetector loopDetector;
        int prevBlock;
        int nextBlock;
        
        protected StreamBlockByteBuffer() throws IOException {
            this.oneByte = new byte[1];
            this.loopDetector = NPOIFSStream.this.blockStore.getChainLoopDetector();
            this.prevBlock = -2;
            this.nextBlock = NPOIFSStream.this.startBlock;
        }
        
        protected void createBlockIfNeeded() throws IOException {
            if (this.buffer != null && this.buffer.hasRemaining()) {
                return;
            }
            int thisBlock = this.nextBlock;
            if (thisBlock == -2) {
                thisBlock = NPOIFSStream.this.blockStore.getFreeBlock();
                this.loopDetector.claim(thisBlock);
                this.nextBlock = -2;
                if (this.prevBlock != -2) {
                    NPOIFSStream.this.blockStore.setNextBlock(this.prevBlock, thisBlock);
                }
                NPOIFSStream.this.blockStore.setNextBlock(thisBlock, -2);
                if (NPOIFSStream.this.startBlock == -2) {
                    NPOIFSStream.this.startBlock = thisBlock;
                }
            }
            else {
                this.loopDetector.claim(thisBlock);
                this.nextBlock = NPOIFSStream.this.blockStore.getNextBlock(thisBlock);
            }
            this.buffer = NPOIFSStream.this.blockStore.createBlockIfNeeded(thisBlock);
            this.prevBlock = thisBlock;
        }
        
        @Override
        public void write(final int b) throws IOException {
            this.oneByte[0] = (byte)(b & 0xFF);
            this.write(this.oneByte);
        }
        
        @Override
        public void write(final byte[] b, int off, int len) throws IOException {
            if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
                throw new IndexOutOfBoundsException();
            }
            if (len == 0) {
                return;
            }
            do {
                this.createBlockIfNeeded();
                final int writeBytes = Math.min(this.buffer.remaining(), len);
                this.buffer.put(b, off, writeBytes);
                off += writeBytes;
                len -= writeBytes;
            } while (len > 0);
        }
        
        @Override
        public void close() throws IOException {
            final NPOIFSStream toFree = new NPOIFSStream(NPOIFSStream.this.blockStore, this.nextBlock);
            toFree.free(this.loopDetector);
            NPOIFSStream.this.blockStore.setNextBlock(this.prevBlock, -2);
        }
    }
}
