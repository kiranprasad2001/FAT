// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.property;

import java.io.OutputStream;
import org.apache.poi.poifs.storage.PropertyBlock;
import java.io.IOException;
import org.apache.poi.poifs.storage.RawDataBlockList;
import org.apache.poi.poifs.storage.HeaderBlock;
import org.apache.poi.poifs.common.POIFSBigBlockSize;
import org.apache.poi.poifs.storage.BlockWritable;

public final class PropertyTable extends PropertyTableBase implements BlockWritable
{
    private POIFSBigBlockSize _bigBigBlockSize;
    private BlockWritable[] _blocks;
    
    public PropertyTable(final HeaderBlock headerBlock) {
        super(headerBlock);
        this._bigBigBlockSize = headerBlock.getBigBlockSize();
        this._blocks = null;
    }
    
    public PropertyTable(final HeaderBlock headerBlock, final RawDataBlockList blockList) throws IOException {
        super(headerBlock, PropertyFactory.convertToProperties(blockList.fetchBlocks(headerBlock.getPropertyStart(), -1)));
        this._bigBigBlockSize = headerBlock.getBigBlockSize();
        this._blocks = null;
    }
    
    public void preWrite() {
        final Property[] properties = this._properties.toArray(new Property[this._properties.size()]);
        for (int k = 0; k < properties.length; ++k) {
            properties[k].setIndex(k);
        }
        this._blocks = PropertyBlock.createPropertyBlockArray(this._bigBigBlockSize, this._properties);
        for (int k = 0; k < properties.length; ++k) {
            properties[k].preWrite();
        }
    }
    
    @Override
    public int countBlocks() {
        return (this._blocks == null) ? 0 : this._blocks.length;
    }
    
    @Override
    public void writeBlocks(final OutputStream stream) throws IOException {
        if (this._blocks != null) {
            for (int j = 0; j < this._blocks.length; ++j) {
                this._blocks[j].writeBlocks(stream);
            }
        }
    }
}
