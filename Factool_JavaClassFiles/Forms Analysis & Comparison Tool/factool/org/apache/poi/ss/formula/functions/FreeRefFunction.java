// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ValueEval;

public interface FreeRefFunction
{
    ValueEval evaluate(final ValueEval[] p0, final OperationEvaluationContext p1);
}
