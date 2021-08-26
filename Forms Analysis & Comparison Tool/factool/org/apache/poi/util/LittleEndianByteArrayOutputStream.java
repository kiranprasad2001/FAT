// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.util;

public final class LittleEndianByteArrayOutputStream implements LittleEndianOutput, DelayableLittleEndianOutput
{
    private final byte[] _buf;
    private final int _endIndex;
    private int _writeIndex;
    
    public LittleEndianByteArrayOutputStream(final byte[] buf, final int startOffset, final int maxWriteLen) {
        if (startOffset < 0 || startOffset > buf.length) {
            throw new IllegalArgumentException("Specified startOffset (" + startOffset + ") is out of allowable range (0.." + buf.length + ")");
        }
        this._buf = buf;
        this._writeIndex = startOffset;
        this._endIndex = startOffset + maxWriteLen;
        if (this._endIndex < startOffset || this._endIndex > buf.length) {
            throw new IllegalArgumentException("calculated end index (" + this._endIndex + ") is out of allowable range (" + this._writeIndex + ".." + buf.length + ")");
        }
    }
    
    public LittleEndianByteArrayOutputStream(final byte[] buf, final int startOffset) {
        this(buf, startOffset, buf.length - startOffset);
    }
    
    private void checkPosition(final int i) {
        if (i > this._endIndex - this._writeIndex) {
            throw new RuntimeException("Buffer overrun");
        }
    }
    
    @Override
    public void writeByte(final int v) {
        this.checkPosition(1);
        this._buf[this._writeIndex++] = (byte)v;
    }
    
    @Override
    public void writeDouble(final double v) {
        this.writeLong(Double.doubleToLongBits(v));
    }
    
    @Override
    public void writeInt(final int v) {
        this.checkPosition(4);
        int i = this._writeIndex;
        this._buf[i++] = (byte)(v >>> 0 & 0xFF);
        this._buf[i++] = (byte)(v >>> 8 & 0xFF);
        this._buf[i++] = (byte)(v >>> 16 & 0xFF);
        this._buf[i++] = (byte)(v >>> 24 & 0xFF);
        this._writeIndex = i;
    }
    
    @Override
    public void writeLong(final long v) {
        this.writeInt((int)(v >> 0));
        this.writeInt((int)(v >> 32));
    }
    
    @Override
    public void writeShort(final int v) {
        this.checkPosition(2);
        int i = this._writeIndex;
        this._buf[i++] = (byte)(v >>> 0 & 0xFF);
        this._buf[i++] = (byte)(v >>> 8 & 0xFF);
        this._writeIndex = i;
    }
    
    @Override
    public void write(final byte[] b) {
        final int len = b.length;
        this.checkPosition(len);
        System.arraycopy(b, 0, this._buf, this._writeIndex, len);
        this._writeIndex += len;
    }
    
    @Override
    public void write(final byte[] b, final int offset, final int len) {
        this.checkPosition(len);
        System.arraycopy(b, offset, this._buf, this._writeIndex, len);
        this._writeIndex += len;
    }
    
    public int getWriteIndex() {
        return this._writeIndex;
    }
    
    @Override
    public LittleEndianOutput createDelayedOutput(final int size) {
        this.checkPosition(size);
        final LittleEndianOutput result = new LittleEndianByteArrayOutputStream(this._buf, this._writeIndex, size);
        this._writeIndex += size;
        return result;
    }
}
