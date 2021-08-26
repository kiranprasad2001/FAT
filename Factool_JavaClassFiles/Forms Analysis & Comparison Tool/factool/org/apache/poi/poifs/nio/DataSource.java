// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.nio;

import java.io.OutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class DataSource
{
    public abstract ByteBuffer read(final int p0, final long p1) throws IOException;
    
    public abstract void write(final ByteBuffer p0, final long p1) throws IOException;
    
    public abstract long size() throws IOException;
    
    public abstract void close() throws IOException;
    
    public abstract void copyTo(final OutputStream p0) throws IOException;
}
