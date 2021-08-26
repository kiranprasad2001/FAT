// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.NumericValueEval;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class DMin implements IDStarAlgorithm
{
    private ValueEval minimumValue;
    
    @Override
    public void reset() {
        this.minimumValue = null;
    }
    
    @Override
    public boolean processMatch(final ValueEval eval) {
        if (eval instanceof NumericValueEval) {
            if (this.minimumValue == null) {
                this.minimumValue = eval;
            }
            else {
                final double currentValue = ((NumericValueEval)eval).getNumberValue();
                final double oldValue = ((NumericValueEval)this.minimumValue).getNumberValue();
                if (currentValue < oldValue) {
                    this.minimumValue = eval;
                }
            }
        }
        return true;
    }
    
    @Override
    public ValueEval getResult() {
        if (this.minimumValue == null) {
            return NumberEval.ZERO;
        }
        return this.minimumValue;
    }
}
