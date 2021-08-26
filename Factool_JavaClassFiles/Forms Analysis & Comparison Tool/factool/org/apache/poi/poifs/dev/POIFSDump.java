// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.dev;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.io.FileOutputStream;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.DocumentNode;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import java.io.File;
import java.io.InputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import java.io.FileInputStream;

public class POIFSDump
{
    public static void main(final String[] args) throws Exception {
        for (int i = 0; i < args.length; ++i) {
            System.out.println("Dumping " + args[i]);
            final FileInputStream is = new FileInputStream(args[i]);
            final POIFSFileSystem fs = new POIFSFileSystem(is);
            is.close();
            final DirectoryEntry root = fs.getRoot();
            final File file = new File(root.getName());
            file.mkdir();
            dump(root, file);
        }
    }
    
    public static void dump(final DirectoryEntry root, final File parent) throws IOException {
        final Iterator<Entry> it = root.getEntries();
        while (it.hasNext()) {
            final Entry entry = it.next();
            if (entry instanceof DocumentNode) {
                final DocumentNode node = (DocumentNode)entry;
                final DocumentInputStream is = new DocumentInputStream(node);
                final byte[] bytes = new byte[node.getSize()];
                is.read(bytes);
                is.close();
                final OutputStream out = new FileOutputStream(new File(parent, node.getName().trim()));
                try {
                    out.write(bytes);
                }
                finally {
                    out.close();
                }
            }
            else if (entry instanceof DirectoryEntry) {
                final DirectoryEntry dir = (DirectoryEntry)entry;
                final File file = new File(parent, entry.getName());
                file.mkdir();
                dump(dir, file);
            }
            else {
                System.err.println("Skipping unsupported POIFS entry: " + entry);
            }
        }
    }
}
