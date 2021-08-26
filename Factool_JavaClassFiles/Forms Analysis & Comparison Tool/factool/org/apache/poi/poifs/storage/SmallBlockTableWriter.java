// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.storage;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import org.apache.poi.poifs.filesystem.POIFSDocument;
import java.util.ArrayList;
import org.apache.poi.poifs.common.POIFSBigBlockSize;
import org.apache.poi.poifs.property.RootProperty;
import java.util.List;
import org.apache.poi.poifs.filesystem.BATManaged;

public class SmallBlockTableWriter implements BlockWritable, BATManaged
{
    private BlockAllocationTableWriter _sbat;
    private List _small_blocks;
    private int _big_block_count;
    private RootProperty _root;
    
    public SmallBlockTableWriter(final POIFSBigBlockSize bigBlockSize, final List documents, final RootProperty root) {
        this._sbat = new BlockAllocationTableWriter(bigBlockSize);
        this._small_blocks = new ArrayList();
        this._root = root;
        for (final POIFSDocument doc : documents) {
            final BlockWritable[] blocks = doc.getSmallBlocks();
            if (blocks.length != 0) {
                doc.setStartBlock(this._sbat.allocateSpace(blocks.length));
                for (int j = 0; j < blocks.length; ++j) {
                    this._small_blocks.add(blocks[j]);
                }
            }
            else {
                doc.setStartBlock(-2);
            }
        }
        this._sbat.simpleCreateBlocks();
        this._root.setSize(this._small_blocks.size());
        this._big_block_count = SmallDocumentBlock.fill(bigBlockSize, this._small_blocks);
    }
    
    public int getSBATBlockCount() {
        return (this._big_block_count + 15) / 16;
    }
    
    public BlockAllocationTableWriter getSBAT() {
        return this._sbat;
    }
    
    @Override
    public int countBlocks() {
        return this._big_block_count;
    }
    
    @Override
    public void setStartBlock(final int start_block) {
        this._root.setStartBlock(start_block);
    }
    
    @Override
    public void writeBlocks(final OutputStream stream) throws IOException {
        final Iterator iter = this._small_blocks.iterator();
        while (iter.hasNext()) {
            iter.next().writeBlocks(stream);
        }
    }
}
