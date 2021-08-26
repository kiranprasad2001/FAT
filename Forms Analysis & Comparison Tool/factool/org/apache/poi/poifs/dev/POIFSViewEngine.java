// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.dev;

import java.io.IOException;
import java.io.Reader;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public class POIFSViewEngine
{
    private static final String _EOL;
    
    public static List<String> inspectViewable(final Object viewable, final boolean drilldown, final int indentLevel, final String indentString) {
        final List<String> objects = new ArrayList<String>();
        if (viewable instanceof POIFSViewable) {
            final POIFSViewable inspected = (POIFSViewable)viewable;
            objects.add(indent(indentLevel, indentString, inspected.getShortDescription()));
            if (drilldown) {
                if (inspected.preferArray()) {
                    final Object[] data = inspected.getViewableArray();
                    for (int j = 0; j < data.length; ++j) {
                        objects.addAll(inspectViewable(data[j], drilldown, indentLevel + 1, indentString));
                    }
                }
                else {
                    final Iterator<Object> iter = inspected.getViewableIterator();
                    while (iter.hasNext()) {
                        objects.addAll(inspectViewable(iter.next(), drilldown, indentLevel + 1, indentString));
                    }
                }
            }
        }
        else {
            objects.add(indent(indentLevel, indentString, viewable.toString()));
        }
        return objects;
    }
    
    private static String indent(final int indentLevel, final String indentString, final String data) {
        final StringBuffer finalBuffer = new StringBuffer();
        final StringBuffer indentPrefix = new StringBuffer();
        for (int j = 0; j < indentLevel; ++j) {
            indentPrefix.append(indentString);
        }
        final LineNumberReader reader = new LineNumberReader(new StringReader(data));
        try {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                finalBuffer.append(indentPrefix).append(line).append(POIFSViewEngine._EOL);
            }
        }
        catch (IOException e) {
            finalBuffer.append(indentPrefix).append(e.getMessage()).append(POIFSViewEngine._EOL);
        }
        return finalBuffer.toString();
    }
    
    static {
        _EOL = System.getProperty("line.separator");
    }
}
