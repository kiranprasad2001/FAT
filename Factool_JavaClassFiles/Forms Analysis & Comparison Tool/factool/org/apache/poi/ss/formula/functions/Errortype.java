// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class Errortype extends Fixed1ArgFunction
{
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0) {
        try {
            OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
            return ErrorEval.NA;
        }
        catch (EvaluationException e) {
            final int result = this.translateErrorCodeToErrorTypeValue(e.getErrorEval().getErrorCode());
            return new NumberEval(result);
        }
    }
    
    private int translateErrorCodeToErrorTypeValue(final int errorCode) {
        switch (errorCode) {
            case 0: {
                return 1;
            }
            case 7: {
                return 2;
            }
            case 15: {
                return 3;
            }
            case 23: {
                return 4;
            }
            case 29: {
                return 5;
            }
            case 36: {
                return 6;
            }
            case 42: {
                return 7;
            }
            default: {
                throw new IllegalArgumentException("Invalid error code (" + errorCode + ")");
            }
        }
    }
}
