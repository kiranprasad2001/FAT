// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.filesystem;

import java.util.Map;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Collection;
import java.util.List;
import java.io.IOException;
import java.util.Iterator;
import java.io.InputStream;
import org.apache.poi.util.Internal;

@Internal
public class EntryUtils
{
    @Internal
    public static void copyNodeRecursively(final Entry entry, final DirectoryEntry target) throws IOException {
        DirectoryEntry newTarget = null;
        if (entry.isDirectoryEntry()) {
            final DirectoryEntry dirEntry = (DirectoryEntry)entry;
            newTarget = target.createDirectory(entry.getName());
            newTarget.setStorageClsid(dirEntry.getStorageClsid());
            final Iterator<Entry> entries = dirEntry.getEntries();
            while (entries.hasNext()) {
                copyNodeRecursively(entries.next(), newTarget);
            }
        }
        else {
            final DocumentEntry dentry = (DocumentEntry)entry;
            final DocumentInputStream dstream = new DocumentInputStream(dentry);
            target.createDocument(dentry.getName(), dstream);
            dstream.close();
        }
    }
    
    public static void copyNodes(final DirectoryEntry sourceRoot, final DirectoryEntry targetRoot) throws IOException {
        for (final Entry entry : sourceRoot) {
            copyNodeRecursively(entry, targetRoot);
        }
    }
    
    public static void copyNodes(final FilteringDirectoryNode filteredSource, final FilteringDirectoryNode filteredTarget) throws IOException {
        copyNodes(filteredSource, (DirectoryEntry)filteredTarget);
    }
    
    @Deprecated
    public static void copyNodes(final DirectoryEntry sourceRoot, final DirectoryEntry targetRoot, final List<String> excepts) throws IOException {
        final Iterator<Entry> entries = sourceRoot.getEntries();
        while (entries.hasNext()) {
            final Entry entry = entries.next();
            if (!excepts.contains(entry.getName())) {
                copyNodeRecursively(entry, targetRoot);
            }
        }
    }
    
    public static void copyNodes(final POIFSFileSystem source, final POIFSFileSystem target) throws IOException {
        copyNodes(source.getRoot(), target.getRoot());
    }
    
    public static void copyNodes(final POIFSFileSystem source, final POIFSFileSystem target, final List<String> excepts) throws IOException {
        copyNodes(new FilteringDirectoryNode(source.getRoot(), excepts), new FilteringDirectoryNode(target.getRoot(), excepts));
    }
    
    public static boolean areDirectoriesIdentical(final DirectoryEntry dirA, final DirectoryEntry dirB) {
        if (!dirA.getName().equals(dirB.getName())) {
            return false;
        }
        if (dirA.getEntryCount() != dirB.getEntryCount()) {
            return false;
        }
        final Map<String, Integer> aSizes = new HashMap<String, Integer>();
        final int isDirectory = -12345;
        for (final Entry a : dirA) {
            final String aName = a.getName();
            if (a.isDirectoryEntry()) {
                aSizes.put(aName, -12345);
            }
            else {
                aSizes.put(aName, ((DocumentNode)a).getSize());
            }
        }
        for (final Entry b : dirB) {
            final String bName = b.getName();
            if (!aSizes.containsKey(bName)) {
                return false;
            }
            int size;
            if (b.isDirectoryEntry()) {
                size = -12345;
            }
            else {
                size = ((DocumentNode)b).getSize();
            }
            if (size != aSizes.get(bName)) {
                return false;
            }
            aSizes.remove(bName);
        }
        if (!aSizes.isEmpty()) {
            return false;
        }
        for (final Entry a : dirA) {
            try {
                final Entry b2 = dirB.getEntry(a.getName());
                boolean match;
                if (a.isDirectoryEntry()) {
                    match = areDirectoriesIdentical((DirectoryEntry)a, (DirectoryEntry)b2);
                }
                else {
                    match = areDocumentsIdentical((DocumentEntry)a, (DocumentEntry)b2);
                }
                if (!match) {
                    return false;
                }
                continue;
            }
            catch (FileNotFoundException e) {
                return false;
            }
            catch (IOException e2) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean areDocumentsIdentical(final DocumentEntry docA, final DocumentEntry docB) throws IOException {
        if (!docA.getName().equals(docB.getName())) {
            return false;
        }
        if (docA.getSize() != docB.getSize()) {
            return false;
        }
        boolean matches = true;
        DocumentInputStream inpA = null;
        DocumentInputStream inpB = null;
        try {
            inpA = new DocumentInputStream(docA);
            inpB = new DocumentInputStream(docB);
            int readA;
            int readB;
            do {
                readA = inpA.read();
                readB = inpB.read();
                if (readA != readB) {
                    matches = false;
                    break;
                }
            } while (readA != -1 && readB != -1);
        }
        finally {
            if (inpA != null) {
                inpA.close();
            }
            if (inpB != null) {
                inpB.close();
            }
        }
        return matches;
    }
}
