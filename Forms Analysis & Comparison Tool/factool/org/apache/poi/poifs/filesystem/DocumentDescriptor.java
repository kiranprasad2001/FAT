// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.filesystem;

public class DocumentDescriptor
{
    private POIFSDocumentPath path;
    private String name;
    private int hashcode;
    
    public DocumentDescriptor(final POIFSDocumentPath path, final String name) {
        this.hashcode = 0;
        if (path == null) {
            throw new NullPointerException("path must not be null");
        }
        if (name == null) {
            throw new NullPointerException("name must not be null");
        }
        if (name.length() == 0) {
            throw new IllegalArgumentException("name cannot be empty");
        }
        this.path = path;
        this.name = name;
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean rval = false;
        if (o != null && o.getClass() == this.getClass()) {
            if (this == o) {
                rval = true;
            }
            else {
                final DocumentDescriptor descriptor = (DocumentDescriptor)o;
                rval = (this.path.equals(descriptor.path) && this.name.equals(descriptor.name));
            }
        }
        return rval;
    }
    
    @Override
    public int hashCode() {
        if (this.hashcode == 0) {
            this.hashcode = (this.path.hashCode() ^ this.name.hashCode());
        }
        return this.hashcode;
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer(40 * (this.path.length() + 1));
        for (int j = 0; j < this.path.length(); ++j) {
            buffer.append(this.path.getComponent(j)).append("/");
        }
        buffer.append(this.name);
        return buffer.toString();
    }
}
