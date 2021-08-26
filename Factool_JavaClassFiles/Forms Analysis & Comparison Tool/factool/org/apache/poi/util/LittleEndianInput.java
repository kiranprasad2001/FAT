// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.util;

public interface LittleEndianInput
{
    int available();
    
    byte readByte();
    
    int readUByte();
    
    short readShort();
    
    int readUShort();
    
    int readInt();
    
    long readLong();
    
    double readDouble();
    
    void readFully(final byte[] p0);
    
    void readFully(final byte[] p0, final int p1, final int p2);
}
