// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

public interface Textbox
{
    public static final short OBJECT_TYPE_TEXT = 6;
    
    RichTextString getString();
    
    void setString(final RichTextString p0);
    
    int getMarginLeft();
    
    void setMarginLeft(final int p0);
    
    int getMarginRight();
    
    void setMarginRight(final int p0);
    
    int getMarginTop();
    
    void setMarginTop(final int p0);
    
    int getMarginBottom();
    
    void setMarginBottom(final int p0);
}
