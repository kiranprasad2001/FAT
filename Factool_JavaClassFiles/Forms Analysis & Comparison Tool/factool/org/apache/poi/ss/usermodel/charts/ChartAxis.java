// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel.charts;

public interface ChartAxis
{
    long getId();
    
    AxisPosition getPosition();
    
    void setPosition(final AxisPosition p0);
    
    String getNumberFormat();
    
    void setNumberFormat(final String p0);
    
    boolean isSetLogBase();
    
    void setLogBase(final double p0);
    
    double getLogBase();
    
    boolean isSetMinimum();
    
    double getMinimum();
    
    void setMinimum(final double p0);
    
    boolean isSetMaximum();
    
    double getMaximum();
    
    void setMaximum(final double p0);
    
    AxisOrientation getOrientation();
    
    void setOrientation(final AxisOrientation p0);
    
    void setCrosses(final AxisCrosses p0);
    
    AxisCrosses getCrosses();
    
    void crossAxis(final ChartAxis p0);
    
    boolean isVisible();
    
    void setVisible(final boolean p0);
    
    AxisTickMark getMajorTickMark();
    
    void setMajorTickMark(final AxisTickMark p0);
    
    AxisTickMark getMinorTickMark();
    
    void setMinorTickMark(final AxisTickMark p0);
}
