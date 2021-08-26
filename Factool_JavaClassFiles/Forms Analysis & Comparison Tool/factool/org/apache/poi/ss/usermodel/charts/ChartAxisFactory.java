// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel.charts;

public interface ChartAxisFactory
{
    ValueAxis createValueAxis(final AxisPosition p0);
    
    ChartAxis createCategoryAxis(final AxisPosition p0);
}
