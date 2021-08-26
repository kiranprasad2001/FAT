// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.ValueEval;

public interface Function4Arg extends Function
{
    ValueEval evaluate(final int p0, final int p1, final ValueEval p2, final ValueEval p3, final ValueEval p4, final ValueEval p5);
}
