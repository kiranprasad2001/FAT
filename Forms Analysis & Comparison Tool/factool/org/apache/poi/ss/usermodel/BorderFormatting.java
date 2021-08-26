// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

public interface BorderFormatting
{
    public static final short BORDER_NONE = 0;
    public static final short BORDER_THIN = 1;
    public static final short BORDER_MEDIUM = 2;
    public static final short BORDER_DASHED = 3;
    public static final short BORDER_HAIR = 4;
    public static final short BORDER_THICK = 5;
    public static final short BORDER_DOUBLE = 6;
    public static final short BORDER_DOTTED = 7;
    public static final short BORDER_MEDIUM_DASHED = 8;
    public static final short BORDER_DASH_DOT = 9;
    public static final short BORDER_MEDIUM_DASH_DOT = 10;
    public static final short BORDER_DASH_DOT_DOT = 11;
    public static final short BORDER_MEDIUM_DASH_DOT_DOT = 12;
    public static final short BORDER_SLANTED_DASH_DOT = 13;
    
    short getBorderBottom();
    
    short getBorderDiagonal();
    
    short getBorderLeft();
    
    short getBorderRight();
    
    short getBorderTop();
    
    short getBottomBorderColor();
    
    short getDiagonalBorderColor();
    
    short getLeftBorderColor();
    
    short getRightBorderColor();
    
    short getTopBorderColor();
    
    void setBorderBottom(final short p0);
    
    void setBorderDiagonal(final short p0);
    
    void setBorderLeft(final short p0);
    
    void setBorderRight(final short p0);
    
    void setBorderTop(final short p0);
    
    void setBottomBorderColor(final short p0);
    
    void setDiagonalBorderColor(final short p0);
    
    void setLeftBorderColor(final short p0);
    
    void setRightBorderColor(final short p0);
    
    void setTopBorderColor(final short p0);
}
