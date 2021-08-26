// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.ValueEval;

public final class Intercept extends Fixed2ArgFunction
{
    private final LinearRegressionFunction func;
    
    public Intercept() {
        this.func = new LinearRegressionFunction(LinearRegressionFunction.FUNCTION.INTERCEPT);
    }
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1) {
        return this.func.evaluate(srcRowIndex, srcColumnIndex, arg0, arg1);
    }
}
