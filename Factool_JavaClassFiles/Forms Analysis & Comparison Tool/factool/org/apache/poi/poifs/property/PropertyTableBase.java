// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.property;

import java.util.Stack;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.poifs.storage.HeaderBlock;
import org.apache.poi.poifs.filesystem.BATManaged;

public abstract class PropertyTableBase implements BATManaged
{
    private final HeaderBlock _header_block;
    protected final List<Property> _properties;
    
    public PropertyTableBase(final HeaderBlock header_block) {
        this._header_block = header_block;
        this._properties = new ArrayList<Property>();
        this.addProperty(new RootProperty());
    }
    
    public PropertyTableBase(final HeaderBlock header_block, final List<Property> properties) throws IOException {
        this._header_block = header_block;
        this._properties = properties;
        this.populatePropertyTree(this._properties.get(0));
    }
    
    public void addProperty(final Property property) {
        this._properties.add(property);
    }
    
    public void removeProperty(final Property property) {
        this._properties.remove(property);
    }
    
    public RootProperty getRoot() {
        return this._properties.get(0);
    }
    
    private void populatePropertyTree(final DirectoryProperty root) throws IOException {
        int index = root.getChildIndex();
        if (!Property.isValidIndex(index)) {
            return;
        }
        final Stack<Property> children = new Stack<Property>();
        children.push(this._properties.get(index));
        while (!children.empty()) {
            final Property property = children.pop();
            if (property == null) {
                continue;
            }
            root.addChild(property);
            if (property.isDirectory()) {
                this.populatePropertyTree((DirectoryProperty)property);
            }
            index = property.getPreviousChildIndex();
            if (Property.isValidIndex(index)) {
                children.push(this._properties.get(index));
            }
            index = property.getNextChildIndex();
            if (!Property.isValidIndex(index)) {
                continue;
            }
            children.push(this._properties.get(index));
        }
    }
    
    public int getStartBlock() {
        return this._header_block.getPropertyStart();
    }
    
    @Override
    public void setStartBlock(final int index) {
        this._header_block.setPropertyStart(index);
    }
}
