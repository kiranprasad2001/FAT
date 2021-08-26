// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

import java.awt.Dimension;

public interface Picture
{
    void resize();
    
    void resize(final double p0);
    
    void resize(final double p0, final double p1);
    
    ClientAnchor getPreferredSize();
    
    ClientAnchor getPreferredSize(final double p0, final double p1);
    
    Dimension getImageDimension();
    
    PictureData getPictureData();
    
    ClientAnchor getClientAnchor();
    
    Sheet getSheet();
}
