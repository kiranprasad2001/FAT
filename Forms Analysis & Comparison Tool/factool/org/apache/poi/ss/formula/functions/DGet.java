// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class DGet implements IDStarAlgorithm
{
    private ValueEval result;
    
    @Override
    public void reset() {
        this.result = null;
    }
    
    @Override
    public boolean processMatch(final ValueEval eval) {
        if (this.result == null) {
            this.result = eval;
            return true;
        }
        this.result = ErrorEval.NUM_ERROR;
        return false;
    }
    
    @Override
    public ValueEval getResult() {
        if (this.result == null) {
            return ErrorEval.VALUE_INVALID;
        }
        return this.result;
    }
}
