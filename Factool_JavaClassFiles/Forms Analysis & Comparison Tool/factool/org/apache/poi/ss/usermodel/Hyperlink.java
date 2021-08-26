// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

public interface Hyperlink extends org.apache.poi.common.usermodel.Hyperlink
{
    int getFirstRow();
    
    void setFirstRow(final int p0);
    
    int getLastRow();
    
    void setLastRow(final int p0);
    
    int getFirstColumn();
    
    void setFirstColumn(final int p0);
    
    int getLastColumn();
    
    void setLastColumn(final int p0);
}
