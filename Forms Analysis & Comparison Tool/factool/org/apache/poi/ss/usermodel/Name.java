// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

public interface Name
{
    String getSheetName();
    
    String getNameName();
    
    void setNameName(final String p0);
    
    String getRefersToFormula();
    
    void setRefersToFormula(final String p0);
    
    boolean isFunctionName();
    
    boolean isDeleted();
    
    void setSheetIndex(final int p0);
    
    int getSheetIndex();
    
    String getComment();
    
    void setComment(final String p0);
    
    void setFunction(final boolean p0);
}
