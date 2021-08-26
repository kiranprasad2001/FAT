// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.storage;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import org.apache.poi.poifs.common.POIFSBigBlockSize;
import org.apache.poi.poifs.property.Property;

public final class PropertyBlock extends BigBlock
{
    private Property[] _properties;
    
    private PropertyBlock(final POIFSBigBlockSize bigBlockSize, final Property[] properties, final int offset) {
        super(bigBlockSize);
        this._properties = new Property[bigBlockSize.getPropertiesPerBlock()];
        for (int j = 0; j < this._properties.length; ++j) {
            this._properties[j] = properties[j + offset];
        }
    }
    
    public static BlockWritable[] createPropertyBlockArray(final POIFSBigBlockSize bigBlockSize, final List<Property> properties) {
        final int _properties_per_block = bigBlockSize.getPropertiesPerBlock();
        final int block_count = (properties.size() + _properties_per_block - 1) / _properties_per_block;
        final Property[] to_be_written = new Property[block_count * _properties_per_block];
        System.arraycopy(properties.toArray(new Property[0]), 0, to_be_written, 0, properties.size());
        for (int j = properties.size(); j < to_be_written.length; ++j) {
            to_be_written[j] = new Property() {
                @Override
                protected void preWrite() {
                }
                
                @Override
                public boolean isDirectory() {
                    return false;
                }
            };
        }
        final BlockWritable[] rvalue = new BlockWritable[block_count];
        for (int i = 0; i < block_count; ++i) {
            rvalue[i] = new PropertyBlock(bigBlockSize, to_be_written, i * _properties_per_block);
        }
        return rvalue;
    }
    
    @Override
    void writeData(final OutputStream stream) throws IOException {
        for (int _properties_per_block = this.bigBlockSize.getPropertiesPerBlock(), j = 0; j < _properties_per_block; ++j) {
            this._properties[j].writeData(stream);
        }
    }
}
