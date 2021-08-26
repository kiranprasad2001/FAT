// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.storage;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.util.POILogger;

public class RawDataBlock implements ListManagedBlock
{
    private byte[] _data;
    private boolean _eof;
    private boolean _hasData;
    private static POILogger log;
    
    public RawDataBlock(final InputStream stream) throws IOException {
        this(stream, 512);
    }
    
    public RawDataBlock(final InputStream stream, final int blockSize) throws IOException {
        this._data = new byte[blockSize];
        final int count = IOUtils.readFully(stream, this._data);
        this._hasData = (count > 0);
        if (count == -1) {
            this._eof = true;
        }
        else if (count != blockSize) {
            this._eof = true;
            final String type = " byte" + ((count == 1) ? "" : "s");
            RawDataBlock.log.log(7, "Unable to read entire block; " + count + type + " read before EOF; expected " + blockSize + " bytes. Your document " + "was either written by software that " + "ignores the spec, or has been truncated!");
        }
        else {
            this._eof = false;
        }
    }
    
    public boolean eof() {
        return this._eof;
    }
    
    public boolean hasData() {
        return this._hasData;
    }
    
    @Override
    public String toString() {
        return "RawDataBlock of size " + this._data.length;
    }
    
    @Override
    public byte[] getData() throws IOException {
        if (!this.hasData()) {
            throw new IOException("Cannot return empty data");
        }
        return this._data;
    }
    
    public int getBigBlockSize() {
        return this._data.length;
    }
    
    static {
        RawDataBlock.log = POILogFactory.getLogger(RawDataBlock.class);
    }
}
