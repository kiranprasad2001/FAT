// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.filesystem;

import java.util.Arrays;
import java.io.IOException;
import java.io.OutputStream;

public final class DocumentOutputStream extends OutputStream
{
    private final OutputStream _stream;
    private final int _limit;
    private int _written;
    
    DocumentOutputStream(final OutputStream stream, final int limit) {
        this._stream = stream;
        this._limit = limit;
        this._written = 0;
    }
    
    @Override
    public void write(final int b) throws IOException {
        this.limitCheck(1);
        this._stream.write(b);
    }
    
    @Override
    public void write(final byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        this.limitCheck(len);
        this._stream.write(b, off, len);
    }
    
    @Override
    public void flush() throws IOException {
        this._stream.flush();
    }
    
    @Override
    public void close() {
    }
    
    void writeFiller(final int totalLimit, final byte fill) throws IOException {
        if (totalLimit > this._written) {
            final byte[] filler = new byte[totalLimit - this._written];
            Arrays.fill(filler, fill);
            this._stream.write(filler);
        }
    }
    
    private void limitCheck(final int toBeWritten) throws IOException {
        if (this._written + toBeWritten > this._limit) {
            throw new IOException("tried to write too much data");
        }
        this._written += toBeWritten;
    }
}
