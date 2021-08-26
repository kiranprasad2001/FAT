// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

public interface Comment
{
    void setVisible(final boolean p0);
    
    boolean isVisible();
    
    int getRow();
    
    void setRow(final int p0);
    
    int getColumn();
    
    void setColumn(final int p0);
    
    String getAuthor();
    
    void setAuthor(final String p0);
    
    RichTextString getString();
    
    void setString(final RichTextString p0);
    
    ClientAnchor getClientAnchor();
}
