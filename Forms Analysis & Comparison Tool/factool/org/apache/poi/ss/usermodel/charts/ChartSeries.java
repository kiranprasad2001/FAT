// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel.charts;

import org.apache.poi.ss.util.CellReference;

public interface ChartSeries
{
    void setTitle(final String p0);
    
    void setTitle(final CellReference p0);
    
    String getTitleString();
    
    CellReference getTitleCellReference();
    
    TitleType getTitleType();
}
