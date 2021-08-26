// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.storage;

public final class DataInputBlock
{
    private final byte[] _buf;
    private int _readIndex;
    private int _maxIndex;
    
    DataInputBlock(final byte[] data, final int startOffset) {
        this._buf = data;
        this._readIndex = startOffset;
        this._maxIndex = this._buf.length;
    }
    
    public int available() {
        return this._maxIndex - this._readIndex;
    }
    
    public int readUByte() {
        return this._buf[this._readIndex++] & 0xFF;
    }
    
    public int readUShortLE() {
        int i = this._readIndex;
        final int b0 = this._buf[i++] & 0xFF;
        final int b2 = this._buf[i++] & 0xFF;
        this._readIndex = i;
        return (b2 << 8) + (b0 << 0);
    }
    
    public int readUShortLE(final DataInputBlock prevBlock) {
        int i = prevBlock._buf.length - 1;
        final int b0 = prevBlock._buf[i++] & 0xFF;
        final int b2 = this._buf[this._readIndex++] & 0xFF;
        return (b2 << 8) + (b0 << 0);
    }
    
    public int readIntLE() {
        int i = this._readIndex;
        final int b0 = this._buf[i++] & 0xFF;
        final int b2 = this._buf[i++] & 0xFF;
        final int b3 = this._buf[i++] & 0xFF;
        final int b4 = this._buf[i++] & 0xFF;
        this._readIndex = i;
        return (b4 << 24) + (b3 << 16) + (b2 << 8) + (b0 << 0);
    }
    
    public int readIntLE(final DataInputBlock prevBlock, final int prevBlockAvailable) {
        final byte[] buf = new byte[4];
        this.readSpanning(prevBlock, prevBlockAvailable, buf);
        final int b0 = buf[0] & 0xFF;
        final int b2 = buf[1] & 0xFF;
        final int b3 = buf[2] & 0xFF;
        final int b4 = buf[3] & 0xFF;
        return (b4 << 24) + (b3 << 16) + (b2 << 8) + (b0 << 0);
    }
    
    public long readLongLE() {
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
    
    public long readLongLE(final DataInputBlock prevBlock, final int prevBlockAvailable) {
        final byte[] buf = new byte[8];
        this.readSpanning(prevBlock, prevBlockAvailable, buf);
        final int b0 = buf[0] & 0xFF;
        final int b2 = buf[1] & 0xFF;
        final int b3 = buf[2] & 0xFF;
        final int b4 = buf[3] & 0xFF;
        final int b5 = buf[4] & 0xFF;
        final int b6 = buf[5] & 0xFF;
        final int b7 = buf[6] & 0xFF;
        final int b8 = buf[7] & 0xFF;
        return ((long)b8 << 56) + ((long)b7 << 48) + ((long)b6 << 40) + ((long)b5 << 32) + ((long)b4 << 24) + (b3 << 16) + (b2 << 8) + (b0 << 0);
    }
    
    private void readSpanning(final DataInputBlock prevBlock, final int prevBlockAvailable, final byte[] buf) {
        System.arraycopy(prevBlock._buf, prevBlock._readIndex, buf, 0, prevBlockAvailable);
        final int secondReadLen = buf.length - prevBlockAvailable;
        System.arraycopy(this._buf, 0, buf, prevBlockAvailable, secondReadLen);
        this._readIndex = secondReadLen;
    }
    
    public void readFully(final byte[] buf, final int off, final int len) {
        System.arraycopy(this._buf, this._readIndex, buf, off, len);
        this._readIndex += len;
    }
}
