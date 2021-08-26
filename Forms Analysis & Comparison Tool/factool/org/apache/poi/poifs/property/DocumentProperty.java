// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.property;

import org.apache.poi.poifs.filesystem.POIFSDocument;

public class DocumentProperty extends Property
{
    private POIFSDocument _document;
    
    public DocumentProperty(final String name, final int size) {
        this._document = null;
        this.setName(name);
        this.setSize(size);
        this.setNodeColor((byte)1);
        this.setPropertyType((byte)2);
    }
    
    protected DocumentProperty(final int index, final byte[] array, final int offset) {
        super(index, array, offset);
        this._document = null;
    }
    
    public void setDocument(final POIFSDocument doc) {
        this._document = doc;
    }
    
    public POIFSDocument getDocument() {
        return this._document;
    }
    
    @Override
    public boolean shouldUseSmallBlocks() {
        return super.shouldUseSmallBlocks();
    }
    
    @Override
    public boolean isDirectory() {
        return false;
    }
    
    @Override
    protected void preWrite() {
    }
    
    public void updateSize(final int size) {
        this.setSize(size);
    }
}
