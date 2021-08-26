// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.ptg;

public interface Pxg
{
    int getExternalWorkbookNumber();
    
    String getSheetName();
    
    void setSheetName(final String p0);
    
    String toFormulaString();
}
