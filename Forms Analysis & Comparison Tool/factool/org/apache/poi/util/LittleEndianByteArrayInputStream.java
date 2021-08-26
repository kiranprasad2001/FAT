// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.util;

public final class LittleEndianByteArrayInputStream implements LittleEndianInput
{
    private final byte[] _buf;
    private final int _endIndex;
    private int _readIndex;
    
    public LittleEndianByteArrayInputStream(final byte[] buf, final int startOffset, final int maxReadLen) {
        this._buf = buf;
        this._readIndex = startOffset;
        this._endIndex = startOffset + maxReadLen;
    }
    
    public LittleEndianByteArrayInputStream(final byte[] buf, final int startOffset) {
        this(buf, startOffset, buf.length - startOffset);
    }
    
    public LittleEndianByteArrayInputStream(final byte[] buf) {
        this(buf, 0, buf.length);
    }
    
    @Override
    public int available() {
        return this._endIndex - this._readIndex;
    }
    
    private void checkPosition(final int i) {
        if (i > this._endIndex - this._readIndex) {
            throw new RuntimeException("Buffer overrun");
        }
    }
    
    public int getReadIndex() {
        return this._readIndex;
    }
    
    @Override
    public byte readByte() {
        this.checkPosition(1);
        return this._buf[this._readIndex++];
    }
    
    @Override
    public int readInt() {
        this.checkPosition(4);
        int i = this._readIndex;
        final int b0 = this._buf[i++] & 0xFF;
        final int b2 = this._buf[i++] & 0xFF;
        final int b3 = this._buf[i++] & 0xFF;
        final int b4 = this._buf[i++] & 0xFF;
        this._readIndex = i;
        return (b4 << 24) + (b3 << 16) + (b2 << 8) + (b0 << 0);
    }
    
    @Override
    public long readLong() {
        this.checkPosition(8);
        int i = this._readIndex;
        final int b0 = this._buf[i++] & 0xFF;
        final int b2 = this._buf[i++] & 0xFF;
        final int b3 = this._buf[i++] & 0xFF;
        final int b4 = this._buf[i++] & 0xFF;
        final int b5 = this._buf[i++] & 0xFF;
        final int b6 = this._buf[i++] & 0xFF;
        final int b7 = this._buf[i++] & 0xFF;
        final int b8 = this._buf[i++] & 0xFF;
        this._readIndex = i;
        return ((long)b8 << 56) + ((long)b7 << 48) + ((long)b6 << 40) + ((long)b5 << 32) + ((long)b4 << 24) + (b3 << 16) + (b2 << 8) + (b0 << 0);
    }
    
    @Override
    public short readShort() {
        return (short)this.readUShort();
    }
    
    @Override
    public int readUByte() {
        this.checkPosition(1);
        return this._buf[this._readIndex++] & 0xFF;
    }
    
    @Override
    public int readUShort() {
        this.checkPosition(2);
        int i = this._readIndex;
        final int b0 = this._buf[i++] & 0xFF;
        final int b2 = this._buf[i++] & 0xFF;
        this._readIndex = i;
        return (b2 << 8) + (b0 << 0);
    }
    
    @Override
    public void readFully(final byte[] buf, final int off, final int len) {
        this.checkPosition(len);
        System.arraycopy(this._buf, this._readIndex, buf, off, len);
        this._readIndex += len;
    }
    
    @Override
    public void readFully(final byte[] buf) {
        this.readFully(buf, 0, buf.length);
    }
    
    @Override
    public double readDouble() {
        return Double.longBitsToDouble(this.readLong());
    }
}
