// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.nio;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class ByteArrayBackedDataSource extends DataSource
{
    private byte[] buffer;
    private long size;
    
    public ByteArrayBackedDataSource(final byte[] data, final int size) {
        this.buffer = data;
        this.size = size;
    }
    
    public ByteArrayBackedDataSource(final byte[] data) {
        this(data, data.length);
    }
    
    @Override
    public ByteBuffer read(final int length, final long position) {
        if (position >= this.size) {
            throw new IndexOutOfBoundsException("Unable to read " + length + " bytes from " + position + " in stream of length " + this.size);
        }
        final int toRead = (int)Math.min(length, this.size - position);
        return ByteBuffer.wrap(this.buffer, (int)position, toRead);
    }
    
    @Override
    public void write(final ByteBuffer src, final long position) {
        final long endPosition = position + src.capacity();
        if (endPosition > this.buffer.length) {
            this.extend(endPosition);
        }
        src.get(this.buffer, (int)position, src.capacity());
        if (endPosition > this.size) {
            this.size = endPosition;
        }
    }
    
    private void extend(final long length) {
        long difference = length - this.buffer.length;
        if (difference < this.buffer.length * 0.25) {
            difference = (long)(this.buffer.length * 0.25);
        }
        if (difference < 4096L) {
            difference = 4096L;
        }
        final byte[] nb = new byte[(int)(difference + this.buffer.length)];
        System.arraycopy(this.buffer, 0, nb, 0, (int)this.size);
        this.buffer = nb;
    }
    
    @Override
    public void copyTo(final OutputStream stream) throws IOException {
        stream.write(this.buffer, 0, (int)this.size);
    }
    
    @Override
    public long size() {
        return this.size;
    }
    
    @Override
    public void close() {
        this.buffer = null;
        this.size = -1L;
    }
}
