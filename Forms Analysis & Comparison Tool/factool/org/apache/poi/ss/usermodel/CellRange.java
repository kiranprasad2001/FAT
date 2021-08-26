// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

import java.util.Iterator;

public interface CellRange<C extends Cell> extends Iterable<C>
{
    int getWidth();
    
    int getHeight();
    
    int size();
    
    String getReferenceText();
    
    C getTopLeftCell();
    
    C getCell(final int p0, final int p1);
    
    C[] getFlattenedCells();
    
    C[][] getCells();
    
    Iterator<C> iterator();
}
