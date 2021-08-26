// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula;

import org.apache.poi.ss.formula.eval.ValueEval;

public interface ThreeDEval extends TwoDEval, SheetRange
{
    ValueEval getValue(final int p0, final int p1, final int p2);
}
