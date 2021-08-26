// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

import org.apache.poi.ss.usermodel.charts.ChartData;
import org.apache.poi.ss.usermodel.charts.ChartAxis;
import java.util.List;
import org.apache.poi.ss.usermodel.charts.ChartLegend;
import org.apache.poi.ss.usermodel.charts.ChartAxisFactory;
import org.apache.poi.ss.usermodel.charts.ChartDataFactory;
import org.apache.poi.ss.usermodel.charts.ManuallyPositionable;

public interface Chart extends ManuallyPositionable
{
    ChartDataFactory getChartDataFactory();
    
    ChartAxisFactory getChartAxisFactory();
    
    ChartLegend getOrCreateLegend();
    
    void deleteLegend();
    
    List<? extends ChartAxis> getAxis();
    
    void plot(final ChartData p0, final ChartAxis... p1);
}
