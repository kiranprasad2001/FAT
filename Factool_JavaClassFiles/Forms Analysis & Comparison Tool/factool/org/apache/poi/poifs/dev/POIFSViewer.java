// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.dev;

import java.util.Iterator;
import java.util.List;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import java.io.FileInputStream;

public class POIFSViewer
{
    public static void main(final String[] args) {
        if (args.length < 0) {
            System.err.println("Must specify at least one file to view");
            System.exit(1);
        }
        final boolean printNames = args.length > 1;
        for (int j = 0; j < args.length; ++j) {
            viewFile(args[j], printNames);
        }
    }
    
    private static void viewFile(final String filename, final boolean printName) {
        if (printName) {
            final StringBuffer flowerbox = new StringBuffer();
            flowerbox.append(".");
            for (int j = 0; j < filename.length(); ++j) {
                flowerbox.append("-");
            }
            flowerbox.append(".");
            System.out.println(flowerbox);
            System.out.println("|" + filename + "|");
            System.out.println(flowerbox);
        }
        try {
            final POIFSViewable fs = new POIFSFileSystem(new FileInputStream(filename));
            final List<String> strings = POIFSViewEngine.inspectViewable(fs, true, 0, "  ");
            final Iterator<String> iter = strings.iterator();
            while (iter.hasNext()) {
                System.out.print(iter.next());
            }
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
