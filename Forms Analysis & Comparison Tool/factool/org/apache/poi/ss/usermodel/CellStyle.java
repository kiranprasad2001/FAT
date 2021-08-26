// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

public interface CellStyle
{
    public static final short ALIGN_GENERAL = 0;
    public static final short ALIGN_LEFT = 1;
    public static final short ALIGN_CENTER = 2;
    public static final short ALIGN_RIGHT = 3;
    public static final short ALIGN_FILL = 4;
    public static final short ALIGN_JUSTIFY = 5;
    public static final short ALIGN_CENTER_SELECTION = 6;
    public static final short VERTICAL_TOP = 0;
    public static final short VERTICAL_CENTER = 1;
    public static final short VERTICAL_BOTTOM = 2;
    public static final short VERTICAL_JUSTIFY = 3;
    public static final short BORDER_NONE = 0;
    public static final short BORDER_THIN = 1;
    public static final short BORDER_MEDIUM = 2;
    public static final short BORDER_DASHED = 3;
    public static final short BORDER_HAIR = 7;
    public static final short BORDER_THICK = 5;
    public static final short BORDER_DOUBLE = 6;
    public static final short BORDER_DOTTED = 4;
    public static final short BORDER_MEDIUM_DASHED = 8;
    public static final short BORDER_DASH_DOT = 9;
    public static final short BORDER_MEDIUM_DASH_DOT = 10;
    public static final short BORDER_DASH_DOT_DOT = 11;
    public static final short BORDER_MEDIUM_DASH_DOT_DOT = 12;
    public static final short BORDER_SLANTED_DASH_DOT = 13;
    public static final short NO_FILL = 0;
    public static final short SOLID_FOREGROUND = 1;
    public static final short FINE_DOTS = 2;
    public static final short ALT_BARS = 3;
    public static final short SPARSE_DOTS = 4;
    public static final short THICK_HORZ_BANDS = 5;
    public static final short THICK_VERT_BANDS = 6;
    public static final short THICK_BACKWARD_DIAG = 7;
    public static final short THICK_FORWARD_DIAG = 8;
    public static final short BIG_SPOTS = 9;
    public static final short BRICKS = 10;
    public static final short THIN_HORZ_BANDS = 11;
    public static final short THIN_VERT_BANDS = 12;
    public static final short THIN_BACKWARD_DIAG = 13;
    public static final short THIN_FORWARD_DIAG = 14;
    public static final short SQUARES = 15;
    public static final short DIAMONDS = 16;
    public static final short LESS_DOTS = 17;
    public static final short LEAST_DOTS = 18;
    
    short getIndex();
    
    void setDataFormat(final short p0);
    
    short getDataFormat();
    
    String getDataFormatString();
    
    void setFont(final Font p0);
    
    short getFontIndex();
    
    void setHidden(final boolean p0);
    
    boolean getHidden();
    
    void setLocked(final boolean p0);
    
    boolean getLocked();
    
    void setAlignment(final short p0);
    
    short getAlignment();
    
    void setWrapText(final boolean p0);
    
    boolean getWrapText();
    
    void setVerticalAlignment(final short p0);
    
    short getVerticalAlignment();
    
    void setRotation(final short p0);
    
    short getRotation();
    
    void setIndention(final short p0);
    
    short getIndention();
    
    void setBorderLeft(final short p0);
    
    short getBorderLeft();
    
    void setBorderRight(final short p0);
    
    short getBorderRight();
    
    void setBorderTop(final short p0);
    
    short getBorderTop();
    
    void setBorderBottom(final short p0);
    
    short getBorderBottom();
    
    void setLeftBorderColor(final short p0);
    
    short getLeftBorderColor();
    
    void setRightBorderColor(final short p0);
    
    short getRightBorderColor();
    
    void setTopBorderColor(final short p0);
    
    short getTopBorderColor();
    
    void setBottomBorderColor(final short p0);
    
    short getBottomBorderColor();
    
    void setFillPattern(final short p0);
    
    short getFillPattern();
    
    void setFillBackgroundColor(final short p0);
    
    short getFillBackgroundColor();
    
    Color getFillBackgroundColorColor();
    
    void setFillForegroundColor(final short p0);
    
    short getFillForegroundColor();
    
    Color getFillForegroundColorColor();
    
    void cloneStyleFrom(final CellStyle p0);
    
    void setShrinkToFit(final boolean p0);
    
    boolean getShrinkToFit();
}
