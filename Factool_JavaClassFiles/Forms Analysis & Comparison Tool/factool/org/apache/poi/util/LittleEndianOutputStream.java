// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;

public final class LittleEndianOutputStream extends FilterOutputStream implements LittleEndianOutput
{
    public LittleEndianOutputStream(final OutputStream out) {
        super(out);
    }
    
    @Override
    public void writeByte(final int v) {
        try {
            this.out.write(v);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void writeDouble(final double v) {
        this.writeLong(Double.doubleToLongBits(v));
    }
    
    @Override
    public void writeInt(final int v) {
        final int b3 = v >>> 24 & 0xFF;
        final int b4 = v >>> 16 & 0xFF;
        final int b5 = v >>> 8 & 0xFF;
        final int b6 = v >>> 0 & 0xFF;
        try {
            this.out.write(b6);
            this.out.write(b5);
            this.out.write(b4);
            this.out.write(b3);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void writeLong(final long v) {
        this.writeInt((int)(v >> 0));
        this.writeInt((int)(v >> 32));
    }
    
    @Override
    public void writeShort(final int v) {
        final int b1 = v >>> 8 & 0xFF;
        final int b2 = v >>> 0 & 0xFF;
        try {
            this.out.write(b2);
            this.out.write(b1);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void write(final byte[] b) {
        try {
            super.write(b);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) {
        try {
            super.write(b, off, len);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
