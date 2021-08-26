// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.storage;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.poi.poifs.common.POIFSBigBlockSize;

abstract class BigBlock implements BlockWritable
{
    protected POIFSBigBlockSize bigBlockSize;
    
    protected BigBlock(final POIFSBigBlockSize bigBlockSize) {
        this.bigBlockSize = bigBlockSize;
    }
    
    protected void doWriteData(final OutputStream stream, final byte[] data) throws IOException {
        stream.write(data);
    }
    
    abstract void writeData(final OutputStream p0) throws IOException;
    
    @Override
    public void writeBlocks(final OutputStream stream) throws IOException {
        this.writeData(stream);
    }
}
