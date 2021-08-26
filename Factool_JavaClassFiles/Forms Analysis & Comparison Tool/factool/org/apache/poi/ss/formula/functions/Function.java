// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.ValueEval;

public interface Function
{
    ValueEval evaluate(final ValueEval[] p0, final int p1, final int p2);
}
