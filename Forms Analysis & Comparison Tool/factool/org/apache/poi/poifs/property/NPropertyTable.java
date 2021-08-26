// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.property;

import org.apache.poi.util.POILogFactory;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.io.IOException;
import org.apache.poi.poifs.filesystem.BlockStore;
import org.apache.poi.poifs.filesystem.NPOIFSStream;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.poifs.storage.HeaderBlock;
import org.apache.poi.poifs.common.POIFSBigBlockSize;
import org.apache.poi.util.POILogger;

public final class NPropertyTable extends PropertyTableBase
{
    private static final POILogger _logger;
    private POIFSBigBlockSize _bigBigBlockSize;
    
    public NPropertyTable(final HeaderBlock headerBlock) {
        super(headerBlock);
        this._bigBigBlockSize = headerBlock.getBigBlockSize();
    }
    
    public NPropertyTable(final HeaderBlock headerBlock, final NPOIFSFileSystem filesystem) throws IOException {
        super(headerBlock, buildProperties(new NPOIFSStream(filesystem, headerBlock.getPropertyStart()).iterator(), headerBlock.getBigBlockSize()));
        this._bigBigBlockSize = headerBlock.getBigBlockSize();
    }
    
    private static List<Property> buildProperties(final Iterator<ByteBuffer> dataSource, final POIFSBigBlockSize bigBlockSize) throws IOException {
        final List<Property> properties = new ArrayList<Property>();
        while (dataSource.hasNext()) {
            final ByteBuffer bb = dataSource.next();
            byte[] data;
            if (bb.hasArray() && bb.arrayOffset() == 0 && bb.array().length == bigBlockSize.getBigBlockSize()) {
                data = bb.array();
            }
            else {
                data = new byte[bigBlockSize.getBigBlockSize()];
                int toRead = data.length;
                if (bb.remaining() < bigBlockSize.getBigBlockSize()) {
                    NPropertyTable._logger.log(5, "Short Property Block, ", bb.remaining(), " bytes instead of the expected " + bigBlockSize.getBigBlockSize());
                    toRead = bb.remaining();
                }
                bb.get(data, 0, toRead);
            }
            PropertyFactory.convertToProperties(data, properties);
        }
        return properties;
    }
    
    @Override
    public int countBlocks() {
        final int size = this._properties.size() * 128;
        return (int)Math.ceil(size / this._bigBigBlockSize.getBigBlockSize());
    }
    
    public void preWrite() {
        final List<Property> pList = new ArrayList<Property>();
        int i = 0;
        for (final Property p : this._properties) {
            if (p == null) {
                continue;
            }
            p.setIndex(i++);
            pList.add(p);
        }
        for (final Property p : pList) {
            p.preWrite();
        }
    }
    
    public void write(final NPOIFSStream stream) throws IOException {
        final OutputStream os = stream.getOutputStream();
        for (final Property property : this._properties) {
            if (property != null) {
                property.writeData(os);
            }
        }
        os.close();
        if (this.getStartBlock() != stream.getStartBlock()) {
            this.setStartBlock(stream.getStartBlock());
        }
    }
    
    static {
        _logger = POILogFactory.getLogger(NPropertyTable.class);
    }
}
