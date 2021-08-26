// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.util;

public interface LittleEndianOutput
{
    void writeByte(final int p0);
    
    void writeShort(final int p0);
    
    void writeInt(final int p0);
    
    void writeLong(final long p0);
    
    void writeDouble(final double p0);
    
    void write(final byte[] p0);
    
    void write(final byte[] p0, final int p1, final int p2);
}
