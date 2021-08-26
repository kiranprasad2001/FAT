// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.eval;

import org.apache.poi.ss.formula.ThreeDEval;
import org.apache.poi.ss.formula.TwoDEval;

public interface AreaEval extends TwoDEval, ThreeDEval
{
    int getFirstRow();
    
    int getLastRow();
    
    int getFirstColumn();
    
    int getLastColumn();
    
    ValueEval getAbsoluteValue(final int p0, final int p1);
    
    boolean contains(final int p0, final int p1);
    
    boolean containsColumn(final int p0);
    
    boolean containsRow(final int p0);
    
    int getWidth();
    
    int getHeight();
    
    ValueEval getRelativeValue(final int p0, final int p1);
    
    AreaEval offset(final int p0, final int p1, final int p2, final int p3);
}
