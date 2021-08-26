// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel.charts;

public interface ManualLayout
{
    void setTarget(final LayoutTarget p0);
    
    LayoutTarget getTarget();
    
    void setXMode(final LayoutMode p0);
    
    LayoutMode getXMode();
    
    void setYMode(final LayoutMode p0);
    
    LayoutMode getYMode();
    
    double getX();
    
    void setX(final double p0);
    
    double getY();
    
    void setY(final double p0);
    
    void setWidthMode(final LayoutMode p0);
    
    LayoutMode getWidthMode();
    
    void setHeightMode(final LayoutMode p0);
    
    LayoutMode getHeightMode();
    
    void setWidthRatio(final double p0);
    
    double getWidthRatio();
    
    void setHeightRatio(final double p0);
    
    double getHeightRatio();
}
