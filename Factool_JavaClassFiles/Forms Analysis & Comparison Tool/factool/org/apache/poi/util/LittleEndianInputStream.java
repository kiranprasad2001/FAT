// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;

public class LittleEndianInputStream extends FilterInputStream implements LittleEndianInput
{
    public LittleEndianInputStream(final InputStream is) {
        super(is);
    }
    
    @Override
    public int available() {
        try {
            return super.available();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public byte readByte() {
        return (byte)this.readUByte();
    }
    
    @Override
    public int readUByte() {
        final byte[] buf = { 0 };
        try {
            checkEOF(this.read(buf), 1);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return LittleEndian.getUByte(buf);
    }
    
    @Override
    public double readDouble() {
        return Double.longBitsToDouble(this.readLong());
    }
    
    @Override
    public int readInt() {
        final byte[] buf = new byte[4];
        try {
            checkEOF(this.read(buf), buf.length);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return LittleEndian.getInt(buf);
    }
    
    public long readUInt() {
        final long retNum = this.readInt();
        return retNum & 0xFFFFFFFFL;
    }
    
    @Override
    public long readLong() {
        final byte[] buf = new byte[8];
        try {
            checkEOF(this.read(buf), 8);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return LittleEndian.getLong(buf);
    }
    
    @Override
    public short readShort() {
        return (short)this.readUShort();
    }
    
    @Override
    public int readUShort() {
        final byte[] buf = new byte[2];
        try {
            checkEOF(this.read(buf), 2);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return LittleEndian.getUShort(buf);
    }
    
    private static void checkEOF(final int actualBytes, final int expectedBytes) {
        if (expectedBytes != 0 && (actualBytes == -1 || actualBytes != expectedBytes)) {
            throw new RuntimeException("Unexpected end-of-file");
        }
    }
    
    @Override
    public void readFully(final byte[] buf) {
        this.readFully(buf, 0, buf.length);
    }
    
    @Override
    public void readFully(final byte[] buf, final int off, final int len) {
        try {
            checkEOF(this.read(buf, off, len), len);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
