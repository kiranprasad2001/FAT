// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.storage;

import java.io.IOException;

public interface BlockList
{
    void zap(final int p0);
    
    ListManagedBlock remove(final int p0) throws IOException;
    
    ListManagedBlock[] fetchBlocks(final int p0, final int p1) throws IOException;
    
    void setBAT(final BlockAllocationTableReader p0) throws IOException;
    
    int blockCount();
}
