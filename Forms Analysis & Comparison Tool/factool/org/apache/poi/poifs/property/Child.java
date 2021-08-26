// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.property;

public interface Child
{
    Child getNextChild();
    
    Child getPreviousChild();
    
    void setNextChild(final Child p0);
    
    void setPreviousChild(final Child p0);
}
