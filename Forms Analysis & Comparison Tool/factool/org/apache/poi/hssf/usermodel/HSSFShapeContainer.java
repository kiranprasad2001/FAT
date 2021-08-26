// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import java.util.List;

public interface HSSFShapeContainer extends Iterable<HSSFShape>
{
    List<HSSFShape> getChildren();
    
    void addShape(final HSSFShape p0);
    
    void setCoordinates(final int p0, final int p1, final int p2, final int p3);
    
    void clear();
    
    int getX1();
    
    int getY1();
    
    int getX2();
    
    int getY2();
    
    boolean removeShape(final HSSFShape p0);
}
