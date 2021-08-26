// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.util;

import java.io.IOException;
import java.io.InputStream;

public class BlockingInputStream extends InputStream
{
    protected InputStream is;
    
    public BlockingInputStream(final InputStream is) {
        this.is = is;
    }
    
    @Override
    public int available() throws IOException {
        return this.is.available();
    }
    
    @Override
    public void close() throws IOException {
        this.is.close();
    }
    
    @Override
    public void mark(final int readLimit) {
        this.is.mark(readLimit);
    }
    
    @Override
    public boolean markSupported() {
        return this.is.markSupported();
    }
    
    @Override
    public int read() throws IOException {
        return this.is.read();
    }
    
    @Override
    public int read(final byte[] bf) throws IOException {
        int i;
        int b;
        for (i = 0, b = 4611; i < bf.length; bf[i++] = (byte)b) {
            b = this.is.read();
            if (b == -1) {
                break;
            }
        }
        if (i == 0 && b == -1) {
            return -1;
        }
        return i;
    }
    
    @Override
    public int read(final byte[] bf, final int s, final int l) throws IOException {
        return this.is.read(bf, s, l);
    }
    
    @Override
    public void reset() throws IOException {
        this.is.reset();
    }
    
    @Override
    public long skip(final long n) throws IOException {
        return this.is.skip(n);
    }
}
