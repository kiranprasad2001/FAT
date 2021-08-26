// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.filesystem;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import org.apache.poi.poifs.property.DocumentProperty;
import java.io.OutputStream;

public final class NDocumentOutputStream extends OutputStream
{
    private int _document_size;
    private boolean _closed;
    private NPOIFSDocument _document;
    private DocumentProperty _property;
    private ByteArrayOutputStream _buffer;
    private NPOIFSStream _stream;
    private OutputStream _stream_output;
    
    public NDocumentOutputStream(final DocumentEntry document) throws IOException {
        this._buffer = new ByteArrayOutputStream(4096);
        if (!(document instanceof DocumentNode)) {
            throw new IOException("Cannot open internal document storage, " + document + " not a Document Node");
        }
        this._document_size = 0;
        this._closed = false;
        this._property = (DocumentProperty)((DocumentNode)document).getProperty();
        (this._document = new NPOIFSDocument((DocumentNode)document)).free();
    }
    
    public NDocumentOutputStream(final DirectoryEntry parent, final String name) throws IOException {
        this._buffer = new ByteArrayOutputStream(4096);
        if (!(parent instanceof DirectoryNode)) {
            throw new IOException("Cannot open internal directory storage, " + parent + " not a Directory Node");
        }
        this._document_size = 0;
        this._closed = false;
        final DocumentEntry doc = parent.createDocument(name, new ByteArrayInputStream(new byte[0]));
        this._property = (DocumentProperty)((DocumentNode)doc).getProperty();
        this._document = new NPOIFSDocument((DocumentNode)doc);
    }
    
    private void dieIfClosed() throws IOException {
        if (this._closed) {
            throw new IOException("cannot perform requested operation on a closed stream");
        }
    }
    
    private void checkBufferSize() throws IOException {
        if (this._buffer.size() > 4096) {
            final byte[] data = this._buffer.toByteArray();
            this._buffer = null;
            this.write(data, 0, data.length);
        }
    }
    
    @Override
    public void write(final int b) throws IOException {
        this.dieIfClosed();
        if (this._buffer != null) {
            this._buffer.write(b);
            this.checkBufferSize();
        }
        else {
            this.write(new byte[] { (byte)b });
        }
    }
    
    @Override
    public void write(final byte[] b) throws IOException {
        this.dieIfClosed();
        if (this._buffer != null) {
            this._buffer.write(b);
            this.checkBufferSize();
        }
        else {
            this.write(b, 0, b.length);
        }
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        this.dieIfClosed();
        if (this._buffer != null) {
            this._buffer.write(b, off, len);
            this.checkBufferSize();
        }
        else {
            if (this._stream == null) {
                this._stream = new NPOIFSStream(this._document.getFileSystem());
                this._stream_output = this._stream.getOutputStream();
            }
            this._stream_output.write(b, off, len);
            this._document_size += len;
        }
    }
    
    @Override
    public void close() throws IOException {
        if (this._buffer != null) {
            this._document.replaceContents(new ByteArrayInputStream(this._buffer.toByteArray()));
        }
        else {
            this._stream_output.close();
            this._property.updateSize(this._document_size);
            this._property.setStartBlock(this._stream.getStartBlock());
        }
        this._closed = true;
    }
}
