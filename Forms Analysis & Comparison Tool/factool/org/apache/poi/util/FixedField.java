// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.util;

import java.io.IOException;
import java.io.InputStream;

public interface FixedField
{
    void readFromBytes(final byte[] p0) throws ArrayIndexOutOfBoundsException;
    
    void readFromStream(final InputStream p0) throws IOException, LittleEndian.BufferUnderrunException;
    
    void writeToBytes(final byte[] p0) throws ArrayIndexOutOfBoundsException;
    
    String toString();
}
