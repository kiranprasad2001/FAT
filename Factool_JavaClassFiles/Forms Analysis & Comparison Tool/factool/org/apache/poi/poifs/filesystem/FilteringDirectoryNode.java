// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.filesystem;

import org.apache.poi.hpsf.ClassID;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FilteringDirectoryNode implements DirectoryEntry
{
    private Set<String> excludes;
    private Map<String, List<String>> childExcludes;
    private DirectoryEntry directory;
    
    public FilteringDirectoryNode(final DirectoryEntry directory, final Collection<String> excludes) {
        this.directory = directory;
        this.excludes = new HashSet<String>();
        this.childExcludes = new HashMap<String, List<String>>();
        for (final String excl : excludes) {
            final int splitAt = excl.indexOf(47);
            if (splitAt == -1) {
                this.excludes.add(excl);
            }
            else {
                final String child = excl.substring(0, splitAt);
                final String childExcl = excl.substring(splitAt + 1);
                if (!this.childExcludes.containsKey(child)) {
                    this.childExcludes.put(child, new ArrayList<String>());
                }
                this.childExcludes.get(child).add(childExcl);
            }
        }
    }
    
    @Override
    public DirectoryEntry createDirectory(final String name) throws IOException {
        return this.directory.createDirectory(name);
    }
    
    @Override
    public DocumentEntry createDocument(final String name, final InputStream stream) throws IOException {
        return this.directory.createDocument(name, stream);
    }
    
    @Override
    public DocumentEntry createDocument(final String name, final int size, final POIFSWriterListener writer) throws IOException {
        return this.directory.createDocument(name, size, writer);
    }
    
    @Override
    public Iterator<Entry> getEntries() {
        return new FilteringIterator();
    }
    
    @Override
    public Iterator<Entry> iterator() {
        return this.getEntries();
    }
    
    @Override
    public int getEntryCount() {
        int size = this.directory.getEntryCount();
        for (final String excl : this.excludes) {
            if (this.directory.hasEntry(excl)) {
                --size;
            }
        }
        return size;
    }
    
    @Override
    public Set<String> getEntryNames() {
        final Set<String> names = new HashSet<String>();
        for (final String name : this.directory.getEntryNames()) {
            if (!this.excludes.contains(name)) {
                names.add(name);
            }
        }
        return names;
    }
    
    @Override
    public boolean isEmpty() {
        return this.getEntryCount() == 0;
    }
    
    @Override
    public boolean hasEntry(final String name) {
        return !this.excludes.contains(name) && this.directory.hasEntry(name);
    }
    
    @Override
    public Entry getEntry(final String name) throws FileNotFoundException {
        if (this.excludes.contains(name)) {
            throw new FileNotFoundException(name);
        }
        final Entry entry = this.directory.getEntry(name);
        return this.wrapEntry(entry);
    }
    
    private Entry wrapEntry(final Entry entry) {
        final String name = entry.getName();
        if (this.childExcludes.containsKey(name) && entry instanceof DirectoryEntry) {
            return new FilteringDirectoryNode((DirectoryEntry)entry, this.childExcludes.get(name));
        }
        return entry;
    }
    
    @Override
    public ClassID getStorageClsid() {
        return this.directory.getStorageClsid();
    }
    
    @Override
    public void setStorageClsid(final ClassID clsidStorage) {
        this.directory.setStorageClsid(clsidStorage);
    }
    
    @Override
    public boolean delete() {
        return this.directory.delete();
    }
    
    @Override
    public boolean renameTo(final String newName) {
        return this.directory.renameTo(newName);
    }
    
    @Override
    public String getName() {
        return this.directory.getName();
    }
    
    @Override
    public DirectoryEntry getParent() {
        return this.directory.getParent();
    }
    
    @Override
    public boolean isDirectoryEntry() {
        return true;
    }
    
    @Override
    public boolean isDocumentEntry() {
        return false;
    }
    
    private class FilteringIterator implements Iterator<Entry>
    {
        private Iterator<Entry> parent;
        private Entry next;
        
        private FilteringIterator() {
            this.parent = FilteringDirectoryNode.this.directory.getEntries();
            this.locateNext();
        }
        
        private void locateNext() {
            this.next = null;
            while (this.parent.hasNext() && this.next == null) {
                final Entry e = this.parent.next();
                if (!FilteringDirectoryNode.this.excludes.contains(e.getName())) {
                    this.next = FilteringDirectoryNode.this.wrapEntry(e);
                }
            }
        }
        
        @Override
        public boolean hasNext() {
            return this.next != null;
        }
        
        @Override
        public Entry next() {
            final Entry e = this.next;
            this.locateNext();
            return e;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException("Remove not supported");
        }
    }
}
