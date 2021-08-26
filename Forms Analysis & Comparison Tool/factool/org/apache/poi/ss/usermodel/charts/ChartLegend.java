// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel.charts;

public interface ChartLegend extends ManuallyPositionable
{
    LegendPosition getPosition();
    
    void setPosition(final LegendPosition p0);
    
    boolean isOverlay();
    
    void setOverlay(final boolean p0);
}
