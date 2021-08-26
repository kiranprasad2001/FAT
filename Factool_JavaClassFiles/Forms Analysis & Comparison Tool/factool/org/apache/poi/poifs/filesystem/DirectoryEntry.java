// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.filesystem;

import org.apache.poi.hpsf.ClassID;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.util.Set;
import java.util.Iterator;

public interface DirectoryEntry extends Entry, Iterable<Entry>
{
    Iterator<Entry> getEntries();
    
    Set<String> getEntryNames();
    
    boolean isEmpty();
    
    int getEntryCount();
    
    boolean hasEntry(final String p0);
    
    Entry getEntry(final String p0) throws FileNotFoundException;
    
    DocumentEntry createDocument(final String p0, final InputStream p1) throws IOException;
    
    DocumentEntry createDocument(final String p0, final int p1, final POIFSWriterListener p2) throws IOException;
    
    DirectoryEntry createDirectory(final String p0) throws IOException;
    
    ClassID getStorageClsid();
    
    void setStorageClsid(final ClassID p0);
}
