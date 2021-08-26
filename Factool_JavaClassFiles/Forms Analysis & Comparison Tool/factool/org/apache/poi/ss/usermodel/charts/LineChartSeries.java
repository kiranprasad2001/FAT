// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel.charts;

public interface LineChartSeries extends ChartSeries
{
    ChartDataSource<?> getCategoryAxisData();
    
    ChartDataSource<? extends Number> getValues();
}
