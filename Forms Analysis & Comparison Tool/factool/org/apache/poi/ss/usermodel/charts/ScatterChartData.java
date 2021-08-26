// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel.charts;

import java.util.List;

public interface ScatterChartData extends ChartData
{
    ScatterChartSeries addSerie(final ChartDataSource<?> p0, final ChartDataSource<? extends Number> p1);
    
    List<? extends ScatterChartSeries> getSeries();
}
