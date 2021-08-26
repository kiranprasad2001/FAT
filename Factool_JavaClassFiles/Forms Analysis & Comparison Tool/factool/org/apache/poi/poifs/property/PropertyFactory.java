// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.property;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.poifs.storage.ListManagedBlock;

class PropertyFactory
{
    private PropertyFactory() {
    }
    
    static List<Property> convertToProperties(final ListManagedBlock[] blocks) throws IOException {
        final List<Property> properties = new ArrayList<Property>();
        for (int j = 0; j < blocks.length; ++j) {
            final byte[] data = blocks[j].getData();
            convertToProperties(data, properties);
        }
        return properties;
    }
    
    static void convertToProperties(final byte[] data, final List<Property> properties) throws IOException {
        final int property_count = data.length / 128;
        int offset = 0;
        for (int k = 0; k < property_count; ++k) {
            switch (data[offset + 66]) {
                case 1: {
                    properties.add(new DirectoryProperty(properties.size(), data, offset));
                    break;
                }
                case 2: {
                    properties.add(new DocumentProperty(properties.size(), data, offset));
                    break;
                }
                case 5: {
                    properties.add(new RootProperty(properties.size(), data, offset));
                    break;
                }
                default: {
                    properties.add(null);
                    break;
                }
            }
            offset += 128;
        }
    }
}
