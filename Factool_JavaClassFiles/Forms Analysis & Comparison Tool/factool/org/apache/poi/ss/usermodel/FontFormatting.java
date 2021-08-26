// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

public interface FontFormatting
{
    public static final short SS_NONE = 0;
    public static final short SS_SUPER = 1;
    public static final short SS_SUB = 2;
    public static final byte U_NONE = 0;
    public static final byte U_SINGLE = 1;
    public static final byte U_DOUBLE = 2;
    public static final byte U_SINGLE_ACCOUNTING = 33;
    public static final byte U_DOUBLE_ACCOUNTING = 34;
    
    short getEscapementType();
    
    void setEscapementType(final short p0);
    
    short getFontColorIndex();
    
    void setFontColorIndex(final short p0);
    
    int getFontHeight();
    
    void setFontHeight(final int p0);
    
    short getUnderlineType();
    
    void setUnderlineType(final short p0);
    
    boolean isBold();
    
    boolean isItalic();
    
    void setFontStyle(final boolean p0, final boolean p1);
    
    void resetFontStyle();
}
