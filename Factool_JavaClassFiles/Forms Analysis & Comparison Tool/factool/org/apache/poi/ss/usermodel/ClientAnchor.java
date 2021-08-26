// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

public interface ClientAnchor
{
    public static final int MOVE_AND_RESIZE = 0;
    public static final int MOVE_DONT_RESIZE = 2;
    public static final int DONT_MOVE_AND_RESIZE = 3;
    
    short getCol1();
    
    void setCol1(final int p0);
    
    short getCol2();
    
    void setCol2(final int p0);
    
    int getRow1();
    
    void setRow1(final int p0);
    
    int getRow2();
    
    void setRow2(final int p0);
    
    int getDx1();
    
    void setDx1(final int p0);
    
    int getDy1();
    
    void setDy1(final int p0);
    
    int getDy2();
    
    void setDy2(final int p0);
    
    int getDx2();
    
    void setDx2(final int p0);
    
    void setAnchorType(final int p0);
    
    int getAnchorType();
}
