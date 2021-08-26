// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.filesystem;

import org.apache.poi.util.HexDump;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Collections;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import org.apache.poi.poifs.property.DocumentProperty;
import org.apache.poi.poifs.dev.POIFSViewable;

public final class NPOIFSDocument implements POIFSViewable
{
    private DocumentProperty _property;
    private NPOIFSFileSystem _filesystem;
    private NPOIFSStream _stream;
    private int _block_size;
    
    public NPOIFSDocument(final DocumentNode document) throws IOException {
        this((DocumentProperty)document.getProperty(), ((DirectoryNode)document.getParent()).getNFileSystem());
    }
    
    public NPOIFSDocument(final DocumentProperty property, final NPOIFSFileSystem filesystem) throws IOException {
        this._property = property;
        this._filesystem = filesystem;
        if (property.getSize() < 4096) {
            this._stream = new NPOIFSStream(this._filesystem.getMiniStore(), property.getStartBlock());
            this._block_size = this._filesystem.getMiniStore().getBlockStoreBlockSize();
        }
        else {
            this._stream = new NPOIFSStream(this._filesystem, property.getStartBlock());
            this._block_size = this._filesystem.getBlockStoreBlockSize();
        }
    }
    
    public NPOIFSDocument(final String name, final NPOIFSFileSystem filesystem, final InputStream stream) throws IOException {
        this._filesystem = filesystem;
        final int length = this.store(stream);
        (this._property = new DocumentProperty(name, length)).setStartBlock(this._stream.getStartBlock());
    }
    
    public NPOIFSDocument(final String name, final int size, final NPOIFSFileSystem filesystem, final POIFSWriterListener writer) throws IOException {
        this._filesystem = filesystem;
        if (size < 4096) {
            this._stream = new NPOIFSStream(filesystem.getMiniStore());
            this._block_size = this._filesystem.getMiniStore().getBlockStoreBlockSize();
        }
        else {
            this._stream = new NPOIFSStream(filesystem);
            this._block_size = this._filesystem.getBlockStoreBlockSize();
        }
        final OutputStream innerOs = this._stream.getOutputStream();
        final DocumentOutputStream os = new DocumentOutputStream(innerOs, size);
        final POIFSDocumentPath path = new POIFSDocumentPath(name.split("\\\\"));
        final String docName = path.getComponent(path.length() - 1);
        final POIFSWriterEvent event = new POIFSWriterEvent(os, path, docName, size);
        writer.processPOIFSWriterEvent(event);
        innerOs.close();
        (this._property = new DocumentProperty(name, size)).setStartBlock(this._stream.getStartBlock());
    }
    
    private int store(final InputStream stream) throws IOException {
        final int bigBlockSize = 4096;
        final BufferedInputStream bis = new BufferedInputStream(stream, 4097);
        bis.mark(4096);
        if (bis.skip(4096L) < 4096L) {
            this._stream = new NPOIFSStream(this._filesystem.getMiniStore());
            this._block_size = this._filesystem.getMiniStore().getBlockStoreBlockSize();
        }
        else {
            this._stream = new NPOIFSStream(this._filesystem);
            this._block_size = this._filesystem.getBlockStoreBlockSize();
        }
        bis.reset();
        final OutputStream os = this._stream.getOutputStream();
        final byte[] buf = new byte[1024];
        int length = 0;
        int readBytes;
        while ((readBytes = bis.read(buf)) != -1) {
            os.write(buf, 0, readBytes);
            length += readBytes;
        }
        os.close();
        return length;
    }
    
    void free() throws IOException {
        this._stream.free();
        this._property.setStartBlock(-2);
    }
    
    NPOIFSFileSystem getFileSystem() {
        return this._filesystem;
    }
    
    int getDocumentBlockSize() {
        return this._block_size;
    }
    
    Iterator<ByteBuffer> getBlockIterator() {
        if (this.getSize() > 0) {
            return this._stream.getBlockIterator();
        }
        final List<ByteBuffer> empty = Collections.emptyList();
        return empty.iterator();
    }
    
    public int getSize() {
        return this._property.getSize();
    }
    
    public void replaceContents(final InputStream stream) throws IOException {
        this.free();
        final int size = this.store(stream);
        this._property.setStartBlock(this._stream.getStartBlock());
        this._property.updateSize(size);
    }
    
    DocumentProperty getDocumentProperty() {
        return this._property;
    }
    
    @Override
    public Object[] getViewableArray() {
        final Object[] results = { null };
        String result;
        try {
            if (this.getSize() > 0) {
                final byte[] data = new byte[this.getSize()];
                int offset = 0;
                for (final ByteBuffer buffer : this._stream) {
                    final int length = Math.min(this._block_size, data.length - offset);
                    buffer.get(data, offset, length);
                    offset += length;
                }
                final ByteArrayOutputStream output = new ByteArrayOutputStream();
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
}
