// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.filesystem;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.poi.poifs.property.Property;
import org.apache.poi.poifs.property.DocumentProperty;
import org.apache.poi.poifs.dev.POIFSViewable;

public class DocumentNode extends EntryNode implements DocumentEntry, POIFSViewable
{
    private POIFSDocument _document;
    
    DocumentNode(final DocumentProperty property, final DirectoryNode parent) {
        super(property, parent);
        this._document = property.getDocument();
    }
    
    POIFSDocument getDocument() {
        return this._document;
    }
    
    @Override
    public int getSize() {
        return this.getProperty().getSize();
    }
    
    @Override
    public boolean isDocumentEntry() {
        return true;
    }
    
    @Override
    protected boolean isDeleteOK() {
        return true;
    }
    
    @Override
    public Object[] getViewableArray() {
        return new Object[0];
    }
    
    @Override
    public Iterator<Object> getViewableIterator() {
        final List<Object> components = new ArrayList<Object>();
        components.add(this.getProperty());
        components.add(this._document);
        return components.iterator();
    }
    
    @Override
    public boolean preferArray() {
        return false;
    }
    
    @Override
    public String getShortDescription() {
        return this.getName();
    }
}
