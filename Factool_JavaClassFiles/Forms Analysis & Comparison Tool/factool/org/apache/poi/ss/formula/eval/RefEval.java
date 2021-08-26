// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.eval;

import org.apache.poi.ss.formula.SheetRange;

public interface RefEval extends ValueEval, SheetRange
{
    ValueEval getInnerValueEval(final int p0);
    
    int getColumn();
    
    int getRow();
    
    int getFirstSheetIndex();
    
    int getLastSheetIndex();
    
    int getNumberOfSheets();
    
    AreaEval offset(final int p0, final int p1, final int p2, final int p3);
}
