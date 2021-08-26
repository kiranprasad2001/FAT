// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.storage;

import java.io.IOException;

abstract class BlockListImpl implements BlockList
{
    private ListManagedBlock[] _blocks;
    private BlockAllocationTableReader _bat;
    
    protected BlockListImpl() {
        this._blocks = new ListManagedBlock[0];
        this._bat = null;
    }
    
    protected void setBlocks(final ListManagedBlock[] blocks) {
        this._blocks = blocks;
    }
    
    @Override
    public void zap(final int index) {
        if (index >= 0 && index < this._blocks.length) {
            this._blocks[index] = null;
        }
    }
    
    protected ListManagedBlock get(final int index) {
        return this._blocks[index];
    }
    
    @Override
    public ListManagedBlock remove(final int index) throws IOException {
        ListManagedBlock result = null;
        try {
            result = this._blocks[index];
            if (result == null) {
                throw new IOException("block[ " + index + " ] already removed - " + "does your POIFS have circular or duplicate block references?");
            }
            this._blocks[index] = null;
        }
        catch (ArrayIndexOutOfBoundsException ignored) {
            throw new IOException("Cannot remove block[ " + index + " ]; out of range[ 0 - " + (this._blocks.length - 1) + " ]");
        }
        return result;
    }
    
    @Override
    public ListManagedBlock[] fetchBlocks(final int startBlock, final int headerPropertiesStartBlock) throws IOException {
        if (this._bat == null) {
            throw new IOException("Improperly initialized list: no block allocation table provided");
        }
        return this._bat.fetchBlocks(startBlock, headerPropertiesStartBlock, this);
    }
    
    @Override
    public void setBAT(final BlockAllocationTableReader bat) throws IOException {
        if (this._bat != null) {
            throw new IOException("Attempt to replace existing BlockAllocationTable");
        }
        this._bat = bat;
    }
    
    @Override
    public int blockCount() {
        return this._blocks.length;
    }
    
    protected int remainingBlocks() {
        int c = 0;
        for (int i = 0; i < this._blocks.length; ++i) {
            if (this._blocks[i] != null) {
                ++c;
            }
        }
        return c;
    }
}
