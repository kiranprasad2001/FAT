// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula;

public interface EvaluationCell
{
    Object getIdentityKey();
    
    EvaluationSheet getSheet();
    
    int getRowIndex();
    
    int getColumnIndex();
    
    int getCellType();
    
    double getNumericCellValue();
    
    String getStringCellValue();
    
    boolean getBooleanCellValue();
    
    int getErrorCellValue();
    
    int getCachedFormulaResultType();
}
