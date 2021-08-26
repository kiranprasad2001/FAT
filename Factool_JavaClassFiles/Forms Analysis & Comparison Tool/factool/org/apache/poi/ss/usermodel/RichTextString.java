// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

public interface RichTextString
{
    void applyFont(final int p0, final int p1, final short p2);
    
    void applyFont(final int p0, final int p1, final Font p2);
    
    void applyFont(final Font p0);
    
    void clearFormatting();
    
    String getString();
    
    int length();
    
    int numFormattingRuns();
    
    int getIndexOfFormattingRun(final int p0);
    
    void applyFont(final short p0);
}
