// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.property;

import org.apache.poi.poifs.storage.SmallDocumentBlock;

public final class RootProperty extends DirectoryProperty
{
    private static final String NAME = "Root Entry";
    
    RootProperty() {
        super("Root Entry");
        this.setNodeColor((byte)1);
        this.setPropertyType((byte)5);
        this.setStartBlock(-2);
    }
    
    protected RootProperty(final int index, final byte[] array, final int offset) {
        super(index, array, offset);
    }
    
    public void setSize(final int size) {
        super.setSize(SmallDocumentBlock.calcSize(size));
    }
    
    @Override
    public String getName() {
        return "Root Entry";
    }
}
