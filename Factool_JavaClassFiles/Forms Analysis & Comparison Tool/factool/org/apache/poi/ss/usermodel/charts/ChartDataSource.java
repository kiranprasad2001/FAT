// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel.charts;

public interface ChartDataSource<T>
{
    int getPointCount();
    
    T getPointAt(final int p0);
    
    boolean isReference();
    
    boolean isNumeric();
    
    String getFormulaString();
}
