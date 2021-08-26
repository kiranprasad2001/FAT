// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.filesystem;

public class POIFSWriterEvent
{
    private DocumentOutputStream stream;
    private POIFSDocumentPath path;
    private String documentName;
    private int limit;
    
    POIFSWriterEvent(final DocumentOutputStream stream, final POIFSDocumentPath path, final String documentName, final int limit) {
        this.stream = stream;
        this.path = path;
        this.documentName = documentName;
        this.limit = limit;
    }
    
    public DocumentOutputStream getStream() {
        return this.stream;
    }
    
    public POIFSDocumentPath getPath() {
        return this.path;
    }
    
    public String getName() {
        return this.documentName;
    }
    
    public int getLimit() {
        return this.limit;
    }
}
