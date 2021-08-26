// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.NotImplementedFunctionException;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class NotImplementedFunction implements Function
{
    private final String _functionName;
    
    protected NotImplementedFunction() {
        this._functionName = this.getClass().getName();
    }
    
    public NotImplementedFunction(final String name) {
        this._functionName = name;
    }
    
    @Override
    public ValueEval evaluate(final ValueEval[] operands, final int srcRow, final int srcCol) {
        throw new NotImplementedFunctionException(this._functionName);
    }
    
    public String getFunctionName() {
        return this._functionName;
    }
}
