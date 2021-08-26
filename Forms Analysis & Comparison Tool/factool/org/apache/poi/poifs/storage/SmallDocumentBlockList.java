// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.storage;

import java.io.IOException;
import java.util.List;

public class SmallDocumentBlockList extends BlockListImpl
{
    public SmallDocumentBlockList(final List blocks) {
        this.setBlocks(blocks.toArray(new SmallDocumentBlock[blocks.size()]));
    }
}
