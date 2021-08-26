// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.storage;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import org.apache.poi.poifs.common.POIFSBigBlockSize;
import java.io.InputStream;

public class RawDataBlockList extends BlockListImpl
{
    public RawDataBlockList(final InputStream stream, final POIFSBigBlockSize bigBlockSize) throws IOException {
        final List<RawDataBlock> blocks = new ArrayList<RawDataBlock>();
        RawDataBlock block;
        do {
            block = new RawDataBlock(stream, bigBlockSize.getBigBlockSize());
            if (block.hasData()) {
                blocks.add(block);
            }
        } while (!block.eof());
        this.setBlocks(blocks.toArray(new RawDataBlock[blocks.size()]));
    }
}
