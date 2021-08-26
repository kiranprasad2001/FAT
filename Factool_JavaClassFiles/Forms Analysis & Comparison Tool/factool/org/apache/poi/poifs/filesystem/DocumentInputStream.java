// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.filesystem;

import java.io.IOException;
import org.apache.poi.util.LittleEndianInput;
import java.io.InputStream;

public class DocumentInputStream extends InputStream implements LittleEndianInput
{
    protected static final int EOF = -1;
    protected static final int SIZE_SHORT = 2;
    protected static final int SIZE_INT = 4;
    protected static final int SIZE_LONG = 8;
    private DocumentInputStream delegate;
    
    protected DocumentInputStream() {
    }
    
    public DocumentInputStream(final DocumentEntry document) throws IOException {
        if (!(document instanceof DocumentNode)) {
            throw new IOException("Cannot open internal document storage");
        }
        final DocumentNode documentNode = (DocumentNode)document;
        final DirectoryNode parentNode = (DirectoryNode)document.getParent();
        if (documentNode.getDocument() != null) {
            this.delegate = new ODocumentInputStream(document);
        }
        else if (parentNode.getFileSystem() != null) {
            this.delegate = new ODocumentInputStream(document);
        }
        else {
            if (parentNode.getNFileSystem() == null) {
                throw new IOException("No FileSystem bound on the parent, can't read contents");
            }
            this.delegate = new NDocumentInputStream(document);
        }
    }
    
    public DocumentInputStream(final POIFSDocument document) {
        this.delegate = new ODocumentInputStream(document);
    }
    
    public DocumentInputStream(final NPOIFSDocument document) {
        this.delegate = new NDocumentInputStream(document);
    }
    
    @Override
    public int available() {
        return this.delegate.available();
    }
    
    @Override
    public void close() {
        this.delegate.close();
    }
    
    @Override
    public void mark(final int ignoredReadlimit) {
        this.delegate.mark(ignoredReadlimit);
    }
    
    @Override
    public boolean markSupported() {
        return true;
    }
    
    @Override
    public int read() throws IOException {
        return this.delegate.read();
    }
    
    @Override
    public int read(final byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }
    
    @Override
    public int read(final byte[] b, final int off, final int len) throws IOException {
        return this.delegate.read(b, off, len);
    }
    
    @Override
    public void reset() {
        this.delegate.reset();
    }
    
    @Override
    public long skip(final long n) throws IOException {
        return this.delegate.skip(n);
    }
    
    @Override
    public byte readByte() {
        return this.delegate.readByte();
    }
    
    @Override
    public double readDouble() {
        return this.delegate.readDouble();
    }
    
    @Override
    public short readShort() {
        return (short)this.readUShort();
    }
    
    @Override
    public void readFully(final byte[] buf) {
        this.readFully(buf, 0, buf.length);
    }
    
    @Override
    public void readFully(final byte[] buf, final int off, final int len) {
        this.delegate.readFully(buf, off, len);
    }
    
    @Override
    public long readLong() {
        return this.delegate.readLong();
    }
    
    @Override
    public int readInt() {
        return this.delegate.readInt();
    }
    
    @Override
    public int readUShort() {
        return this.delegate.readUShort();
    }
    
    @Override
    public int readUByte() {
        return this.delegate.readUByte();
    }
    
    public long readUInt() {
        final int i = this.readInt();
        return (long)i & 0xFFFFFFFFL;
    }
}
