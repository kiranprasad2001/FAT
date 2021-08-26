// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.eventfilesystem;

import org.apache.poi.poifs.filesystem.POIFSDocumentPath;
import org.apache.poi.poifs.filesystem.DocumentInputStream;

public class POIFSReaderEvent
{
    private DocumentInputStream stream;
    private POIFSDocumentPath path;
    private String documentName;
    
    POIFSReaderEvent(final DocumentInputStream stream, final POIFSDocumentPath path, final String documentName) {
        this.stream = stream;
        this.path = path;
        this.documentName = documentName;
    }
    
    public DocumentInputStream getStream() {
        return this.stream;
    }
    
    public POIFSDocumentPath getPath() {
        return this.path;
    }
    
    public String getName() {
        return this.documentName;
    }
}
