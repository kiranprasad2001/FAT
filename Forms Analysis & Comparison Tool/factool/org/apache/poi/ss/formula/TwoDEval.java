// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula;

import org.apache.poi.ss.formula.eval.ValueEval;

public interface TwoDEval extends ValueEval
{
    ValueEval getValue(final int p0, final int p1);
    
    int getWidth();
    
    int getHeight();
    
    boolean isRow();
    
    boolean isColumn();
    
    TwoDEval getRow(final int p0);
    
    TwoDEval getColumn(final int p0);
    
    boolean isSubTotal(final int p0, final int p1);
}
