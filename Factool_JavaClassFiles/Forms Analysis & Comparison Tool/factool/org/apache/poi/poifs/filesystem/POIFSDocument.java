// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.filesystem;

import java.util.Collections;
import java.util.Iterator;
import org.apache.poi.util.HexDump;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import org.apache.poi.poifs.storage.DataInputBlock;
import java.util.List;
import java.util.ArrayList;
import java.io.InputStream;
import org.apache.poi.poifs.property.Property;
import java.io.IOException;
import org.apache.poi.poifs.storage.ListManagedBlock;
import org.apache.poi.poifs.common.POIFSConstants;
import org.apache.poi.poifs.storage.RawDataBlock;
import org.apache.poi.poifs.common.POIFSBigBlockSize;
import org.apache.poi.poifs.property.DocumentProperty;
import org.apache.poi.poifs.storage.SmallDocumentBlock;
import org.apache.poi.poifs.storage.DocumentBlock;
import org.apache.poi.poifs.dev.POIFSViewable;
import org.apache.poi.poifs.storage.BlockWritable;

public final class POIFSDocument implements BATManaged, BlockWritable, POIFSViewable
{
    private static final DocumentBlock[] EMPTY_BIG_BLOCK_ARRAY;
    private static final SmallDocumentBlock[] EMPTY_SMALL_BLOCK_ARRAY;
    private DocumentProperty _property;
    private int _size;
    private final POIFSBigBlockSize _bigBigBlockSize;
    private SmallBlockStore _small_store;
    private BigBlockStore _big_store;
    
    public POIFSDocument(final String name, final RawDataBlock[] blocks, final int length) throws IOException {
        this._size = length;
        if (blocks.length == 0) {
            this._bigBigBlockSize = POIFSConstants.SMALLER_BIG_BLOCK_SIZE_DETAILS;
        }
        else {
            this._bigBigBlockSize = ((blocks[0].getBigBlockSize() == 512) ? POIFSConstants.SMALLER_BIG_BLOCK_SIZE_DETAILS : POIFSConstants.LARGER_BIG_BLOCK_SIZE_DETAILS);
        }
        this._big_store = new BigBlockStore(this._bigBigBlockSize, convertRawBlocksToBigBlocks(blocks));
        this._property = new DocumentProperty(name, this._size);
        this._small_store = new SmallBlockStore(this._bigBigBlockSize, POIFSDocument.EMPTY_SMALL_BLOCK_ARRAY);
        this._property.setDocument(this);
    }
    
    private static DocumentBlock[] convertRawBlocksToBigBlocks(final ListManagedBlock[] blocks) throws IOException {
        final DocumentBlock[] result = new DocumentBlock[blocks.length];
        for (int i = 0; i < result.length; ++i) {
            result[i] = new DocumentBlock((RawDataBlock)blocks[i]);
        }
        return result;
    }
    
    private static SmallDocumentBlock[] convertRawBlocksToSmallBlocks(final ListManagedBlock[] blocks) {
        if (blocks instanceof SmallDocumentBlock[]) {
            return (SmallDocumentBlock[])blocks;
        }
        final SmallDocumentBlock[] result = new SmallDocumentBlock[blocks.length];
        System.arraycopy(blocks, 0, result, 0, blocks.length);
        return result;
    }
    
    public POIFSDocument(final String name, final SmallDocumentBlock[] blocks, final int length) {
        this._size = length;
        if (blocks.length == 0) {
            this._bigBigBlockSize = POIFSConstants.SMALLER_BIG_BLOCK_SIZE_DETAILS;
        }
        else {
            this._bigBigBlockSize = blocks[0].getBigBlockSize();
        }
        this._big_store = new BigBlockStore(this._bigBigBlockSize, POIFSDocument.EMPTY_BIG_BLOCK_ARRAY);
        this._property = new DocumentProperty(name, this._size);
        this._small_store = new SmallBlockStore(this._bigBigBlockSize, blocks);
        this._property.setDocument(this);
    }
    
    public POIFSDocument(final String name, final POIFSBigBlockSize bigBlockSize, final ListManagedBlock[] blocks, final int length) throws IOException {
        this._size = length;
        this._bigBigBlockSize = bigBlockSize;
        (this._property = new DocumentProperty(name, this._size)).setDocument(this);
        if (Property.isSmall(this._size)) {
            this._big_store = new BigBlockStore(bigBlockSize, POIFSDocument.EMPTY_BIG_BLOCK_ARRAY);
            this._small_store = new SmallBlockStore(bigBlockSize, convertRawBlocksToSmallBlocks(blocks));
        }
        else {
            this._big_store = new BigBlockStore(bigBlockSize, convertRawBlocksToBigBlocks(blocks));
            this._small_store = new SmallBlockStore(bigBlockSize, POIFSDocument.EMPTY_SMALL_BLOCK_ARRAY);
        }
    }
    
    public POIFSDocument(final String name, final ListManagedBlock[] blocks, final int length) throws IOException {
        this(name, POIFSConstants.SMALLER_BIG_BLOCK_SIZE_DETAILS, blocks, length);
    }
    
    public POIFSDocument(final String name, final POIFSBigBlockSize bigBlockSize, final InputStream stream) throws IOException {
        final List<DocumentBlock> blocks = new ArrayList<DocumentBlock>();
        this._size = 0;
        this._bigBigBlockSize = bigBlockSize;
        DocumentBlock block;
        do {
            block = new DocumentBlock(stream, bigBlockSize);
            final int blockSize = block.size();
            if (blockSize > 0) {
                blocks.add(block);
                this._size += blockSize;
            }
        } while (!block.partiallyRead());
        final DocumentBlock[] bigBlocks = blocks.toArray(new DocumentBlock[blocks.size()]);
        this._big_store = new BigBlockStore(bigBlockSize, bigBlocks);
        (this._property = new DocumentProperty(name, this._size)).setDocument(this);
        if (this._property.shouldUseSmallBlocks()) {
            this._small_store = new SmallBlockStore(bigBlockSize, SmallDocumentBlock.convert(bigBlockSize, bigBlocks, this._size));
            this._big_store = new BigBlockStore(bigBlockSize, new DocumentBlock[0]);
        }
        else {
            this._small_store = new SmallBlockStore(bigBlockSize, POIFSDocument.EMPTY_SMALL_BLOCK_ARRAY);
        }
    }
    
    public POIFSDocument(final String name, final InputStream stream) throws IOException {
        this(name, POIFSConstants.SMALLER_BIG_BLOCK_SIZE_DETAILS, stream);
    }
    
    public POIFSDocument(final String name, final int size, final POIFSBigBlockSize bigBlockSize, final POIFSDocumentPath path, final POIFSWriterListener writer) {
        this._size = size;
        this._bigBigBlockSize = bigBlockSize;
        (this._property = new DocumentProperty(name, this._size)).setDocument(this);
        if (this._property.shouldUseSmallBlocks()) {
            this._small_store = new SmallBlockStore(this._bigBigBlockSize, path, name, size, writer);
            this._big_store = new BigBlockStore(this._bigBigBlockSize, POIFSDocument.EMPTY_BIG_BLOCK_ARRAY);
        }
        else {
            this._small_store = new SmallBlockStore(this._bigBigBlockSize, POIFSDocument.EMPTY_SMALL_BLOCK_ARRAY);
            this._big_store = new BigBlockStore(this._bigBigBlockSize, path, name, size, writer);
        }
    }
    
    public POIFSDocument(final String name, final int size, final POIFSDocumentPath path, final POIFSWriterListener writer) {
        this(name, size, POIFSConstants.SMALLER_BIG_BLOCK_SIZE_DETAILS, path, writer);
    }
    
    public BlockWritable[] getSmallBlocks() {
        return this._small_store.getBlocks();
    }
    
    public int getSize() {
        return this._size;
    }
    
    void read(final byte[] buffer, final int offset) {
        final int len = buffer.length;
        DataInputBlock currentBlock = this.getDataInputBlock(offset);
        int blockAvailable = currentBlock.available();
        if (blockAvailable > len) {
            currentBlock.readFully(buffer, 0, len);
            return;
        }
        int remaining = len;
        int writePos = 0;
        int currentOffset = offset;
        while (remaining > 0) {
            final boolean blockIsExpiring = remaining >= blockAvailable;
            int reqSize;
            if (blockIsExpiring) {
                reqSize = blockAvailable;
            }
            else {
                reqSize = remaining;
            }
            currentBlock.readFully(buffer, writePos, reqSize);
            remaining -= reqSize;
            writePos += reqSize;
            currentOffset += reqSize;
            if (blockIsExpiring) {
                if (currentOffset == this._size) {
                    if (remaining > 0) {
                        throw new IllegalStateException("reached end of document stream unexpectedly");
                    }
                    currentBlock = null;
                    break;
                }
                else {
                    currentBlock = this.getDataInputBlock(currentOffset);
                    blockAvailable = currentBlock.available();
                }
            }
        }
    }
    
    DataInputBlock getDataInputBlock(final int offset) {
        if (offset >= this._size) {
            if (offset > this._size) {
                throw new RuntimeException("Request for Offset " + offset + " doc size is " + this._size);
            }
            return null;
        }
        else {
            if (this._property.shouldUseSmallBlocks()) {
                return SmallDocumentBlock.getDataInputBlock(this._small_store.getBlocks(), offset);
            }
            return DocumentBlock.getDataInputBlock(this._big_store.getBlocks(), offset);
        }
    }
    
    DocumentProperty getDocumentProperty() {
        return this._property;
    }
    
    @Override
    public void writeBlocks(final OutputStream stream) throws IOException {
        this._big_store.writeBlocks(stream);
    }
    
    @Override
    public int countBlocks() {
        return this._big_store.countBlocks();
    }
    
    @Override
    public void setStartBlock(final int index) {
        this._property.setStartBlock(index);
    }
    
    @Override
    public Object[] getViewableArray() {
        final Object[] results = { null };
        String result;
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            BlockWritable[] blocks = null;
            if (this._big_store.isValid()) {
                blocks = this._big_store.getBlocks();
            }
            else if (this._small_store.isValid()) {
                blocks = this._small_store.getBlocks();
            }
            if (blocks != null) {
                for (int k = 0; k < blocks.length; ++k) {
                    blocks[k].writeBlocks(output);
                }
                byte[] data = output.toByteArray();
                if (data.length > this._property.getSize()) {
                    final byte[] tmp = new byte[this._property.getSize()];
                    System.arraycopy(data, 0, tmp, 0, tmp.length);
                    data = tmp;
                }
                output = new ByteArrayOutputStream();
                HexDump.dump(data, 0L, output, 0);
                result = output.toString();
            }
            else {
                result = "<NO DATA>";
            }
        }
        catch (IOException e) {
            result = e.getMessage();
        }
        results[0] = result;
        return results;
    }
    
    @Override
    public Iterator<Object> getViewableIterator() {
        return Collections.emptyList().iterator();
    }
    
    @Override
    public boolean preferArray() {
        return true;
    }
    
    @Override
    public String getShortDescription() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("Document: \"").append(this._property.getName()).append("\"");
        buffer.append(" size = ").append(this.getSize());
        return buffer.toString();
    }
    
    static {
        EMPTY_BIG_BLOCK_ARRAY = new DocumentBlock[0];
        EMPTY_SMALL_BLOCK_ARRAY = new SmallDocumentBlock[0];
    }
    
    private static final class SmallBlockStore
    {
        private SmallDocumentBlock[] _smallBlocks;
        private final POIFSDocumentPath _path;
        private final String _name;
        private final int _size;
        private final POIFSWriterListener _writer;
        private final POIFSBigBlockSize _bigBlockSize;
        
        SmallBlockStore(final POIFSBigBlockSize bigBlockSize, final SmallDocumentBlock[] blocks) {
            this._bigBlockSize = bigBlockSize;
            this._smallBlocks = blocks.clone();
            this._path = null;
            this._name = null;
            this._size = -1;
            this._writer = null;
        }
        
        SmallBlockStore(final POIFSBigBlockSize bigBlockSize, final POIFSDocumentPath path, final String name, final int size, final POIFSWriterListener writer) {
            this._bigBlockSize = bigBlockSize;
            this._smallBlocks = new SmallDocumentBlock[0];
            this._path = path;
            this._name = name;
            this._size = size;
            this._writer = writer;
        }
        
        boolean isValid() {
            return this._smallBlocks.length > 0 || this._writer != null;
        }
        
        SmallDocumentBlock[] getBlocks() {
            if (this.isValid() && this._writer != null) {
                final ByteArrayOutputStream stream = new ByteArrayOutputStream(this._size);
                final DocumentOutputStream dstream = new DocumentOutputStream(stream, this._size);
                this._writer.processPOIFSWriterEvent(new POIFSWriterEvent(dstream, this._path, this._name, this._size));
                this._smallBlocks = SmallDocumentBlock.convert(this._bigBlockSize, stream.toByteArray(), this._size);
            }
            return this._smallBlocks;
        }
    }
    
    private static final class BigBlockStore
    {
        private DocumentBlock[] bigBlocks;
        private final POIFSDocumentPath _path;
        private final String _name;
        private final int _size;
        private final POIFSWriterListener _writer;
        private final POIFSBigBlockSize _bigBlockSize;
        
        BigBlockStore(final POIFSBigBlockSize bigBlockSize, final DocumentBlock[] blocks) {
            this._bigBlockSize = bigBlockSize;
            this.bigBlocks = blocks.clone();
            this._path = null;
            this._name = null;
            this._size = -1;
            this._writer = null;
        }
        
        BigBlockStore(final POIFSBigBlockSize bigBlockSize, final POIFSDocumentPath path, final String name, final int size, final POIFSWriterListener writer) {
            this._bigBlockSize = bigBlockSize;
            this.bigBlocks = new DocumentBlock[0];
            this._path = path;
            this._name = name;
            this._size = size;
            this._writer = writer;
        }
        
        boolean isValid() {
            return this.bigBlocks.length > 0 || this._writer != null;
        }
        
        DocumentBlock[] getBlocks() {
            if (this.isValid() && this._writer != null) {
                final ByteArrayOutputStream stream = new ByteArrayOutputStream(this._size);
                final DocumentOutputStream dstream = new DocumentOutputStream(stream, this._size);
                this._writer.processPOIFSWriterEvent(new POIFSWriterEvent(dstream, this._path, this._name, this._size));
                this.bigBlocks = DocumentBlock.convert(this._bigBlockSize, stream.toByteArray(), this._size);
            }
            return this.bigBlocks;
        }
        
        void writeBlocks(final OutputStream stream) throws IOException {
            if (this.isValid()) {
                if (this._writer != null) {
                    final DocumentOutputStream dstream = new DocumentOutputStream(stream, this._size);
                    this._writer.processPOIFSWriterEvent(new POIFSWriterEvent(dstream, this._path, this._name, this._size));
                    dstream.writeFiller(this.countBlocks() * this._bigBlockSize.getBigBlockSize(), DocumentBlock.getFillByte());
                }
                else {
                    for (int k = 0; k < this.bigBlocks.length; ++k) {
                        this.bigBlocks[k].writeBlocks(stream);
                    }
                }
            }
        }
        
        int countBlocks() {
            if (!this.isValid()) {
                return 0;
            }
            if (this._writer == null) {
                return this.bigBlocks.length;
            }
            return (this._size + this._bigBlockSize.getBigBlockSize() - 1) / this._bigBlockSize.getBigBlockSize();
        }
    }
}
