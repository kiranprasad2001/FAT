// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.property;

import java.io.IOException;
import java.util.Iterator;
import java.util.Comparator;
import java.util.Arrays;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;

public class DirectoryProperty extends Property implements Parent
{
    private List<Property> _children;
    private Set<String> _children_names;
    
    public DirectoryProperty(final String name) {
        this._children = new ArrayList<Property>();
        this._children_names = new HashSet<String>();
        this.setName(name);
        this.setSize(0);
        this.setPropertyType((byte)1);
        this.setStartBlock(0);
        this.setNodeColor((byte)1);
    }
    
    protected DirectoryProperty(final int index, final byte[] array, final int offset) {
        super(index, array, offset);
        this._children = new ArrayList<Property>();
        this._children_names = new HashSet<String>();
    }
    
    public boolean changeName(final Property property, final String newName) {
        final String oldName = property.getName();
        property.setName(newName);
        final String cleanNewName = property.getName();
        boolean result;
        if (this._children_names.contains(cleanNewName)) {
            property.setName(oldName);
            result = false;
        }
        else {
            this._children_names.add(cleanNewName);
            this._children_names.remove(oldName);
            result = true;
        }
        return result;
    }
    
    public boolean deleteChild(final Property property) {
        final boolean result = this._children.remove(property);
        if (result) {
            this._children_names.remove(property.getName());
        }
        return result;
    }
    
    @Override
    public boolean isDirectory() {
        return true;
    }
    
    @Override
    protected void preWrite() {
        if (this._children.size() > 0) {
            final Property[] children = this._children.toArray(new Property[0]);
            Arrays.sort(children, new PropertyComparator());
            final int midpoint = children.length / 2;
            this.setChildProperty(children[midpoint].getIndex());
            children[0].setPreviousChild(null);
            children[0].setNextChild(null);
            for (int j = 1; j < midpoint; ++j) {
                children[j].setPreviousChild(children[j - 1]);
                children[j].setNextChild(null);
            }
            if (midpoint != 0) {
                children[midpoint].setPreviousChild(children[midpoint - 1]);
            }
            if (midpoint != children.length - 1) {
                children[midpoint].setNextChild(children[midpoint + 1]);
                for (int j = midpoint + 1; j < children.length - 1; ++j) {
                    children[j].setPreviousChild(null);
                    children[j].setNextChild(children[j + 1]);
                }
                children[children.length - 1].setPreviousChild(null);
                children[children.length - 1].setNextChild(null);
            }
            else {
                children[midpoint].setNextChild(null);
            }
        }
    }
    
    @Override
    public Iterator<Property> getChildren() {
        return this._children.iterator();
    }
    
    @Override
    public void addChild(final Property property) throws IOException {
        final String name = property.getName();
        if (this._children_names.contains(name)) {
            throw new IOException("Duplicate name \"" + name + "\"");
        }
        this._children_names.add(name);
        this._children.add(property);
    }
    
    public static class PropertyComparator implements Comparator<Property>
    {
        @Override
        public int compare(final Property o1, final Property o2) {
            final String VBA_PROJECT = "_VBA_PROJECT";
            final String name1 = o1.getName();
            final String name2 = o2.getName();
            int result = name1.length() - name2.length();
            if (result == 0) {
                if (name1.compareTo(VBA_PROJECT) == 0) {
                    result = 1;
                }
                else if (name2.compareTo(VBA_PROJECT) == 0) {
                    result = -1;
                }
                else if (name1.startsWith("__") && name2.startsWith("__")) {
                    result = name1.compareToIgnoreCase(name2);
                }
                else if (name1.startsWith("__")) {
                    result = 1;
                }
                else if (name2.startsWith("__")) {
                    result = -1;
                }
                else {
                    result = name1.compareToIgnoreCase(name2);
                }
            }
            return result;
        }
    }
}
