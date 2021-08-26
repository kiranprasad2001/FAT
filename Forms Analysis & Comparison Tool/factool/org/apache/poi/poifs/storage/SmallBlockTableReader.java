// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.storage;

import java.io.IOException;
import org.apache.poi.poifs.property.RootProperty;
import org.apache.poi.poifs.common.POIFSBigBlockSize;

public final class SmallBlockTableReader
{
    public static BlockList getSmallDocumentBlocks(final POIFSBigBlockSize bigBlockSize, final RawDataBlockList blockList, final RootProperty root, final int sbatStart) throws IOException {
        final ListManagedBlock[] smallBlockBlocks = blockList.fetchBlocks(root.getStartBlock(), -1);
        final BlockList list = new SmallDocumentBlockList(SmallDocumentBlock.extract(bigBlockSize, smallBlockBlocks));
        new BlockAllocationTableReader(bigBlockSize, blockList.fetchBlocks(sbatStart, -1), list);
        return list;
    }
}
