// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.eval;

import org.apache.poi.ss.formula.functions.Function;
import org.apache.poi.ss.formula.functions.Fixed2ArgFunction;

public abstract class TwoOperandNumericOperation extends Fixed2ArgFunction
{
    public static final Function AddEval;
    public static final Function DivideEval;
    public static final Function MultiplyEval;
    public static final Function PowerEval;
    public static final Function SubtractEval;
    
    protected final double singleOperandEvaluate(final ValueEval arg, final int srcCellRow, final int srcCellCol) throws EvaluationException {
        final ValueEval ve = OperandResolver.getSingleValue(arg, srcCellRow, srcCellCol);
        return OperandResolver.coerceValueToDouble(ve);
    }
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1) {
        double result;
        try {
            final double d0 = this.singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex);
            final double d2 = this.singleOperandEvaluate(arg1, srcRowIndex, srcColumnIndex);
            result = this.evaluate(d0, d2);
            if (result == 0.0 && !(this instanceof SubtractEvalClass)) {
                return NumberEval.ZERO;
            }
            if (Double.isNaN(result) || Double.isInfinite(result)) {
                return ErrorEval.NUM_ERROR;
            }
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        return new NumberEval(result);
    }
    
    protected abstract double evaluate(final double p0, final double p1) throws EvaluationException;
    
    static {
        AddEval = new TwoOperandNumericOperation() {
            @Override
            protected double evaluate(final double d0, final double d1) {
                return d0 + d1;
            }
        };
        DivideEval = new TwoOperandNumericOperation() {
            @Override
            protected double evaluate(final double d0, final double d1) throws EvaluationException {
                if (d1 == 0.0) {
                    throw new EvaluationException(ErrorEval.DIV_ZERO);
                }
                return d0 / d1;
            }
        };
        MultiplyEval = new TwoOperandNumericOperation() {
            @Override
            protected double evaluate(final double d0, final double d1) {
                return d0 * d1;
            }
        };
        PowerEval = new TwoOperandNumericOperation() {
            @Override
            protected double evaluate(final double d0, final double d1) {
                return Math.pow(d0, d1);
            }
        };
        SubtractEval = new SubtractEvalClass();
    }
    
    private static final class SubtractEvalClass extends TwoOperandNumericOperation
    {
        public SubtractEvalClass() {
        }
        
        @Override
        protected double evaluate(final double d0, final double d1) {
            return d0 - d1;
        }
    }
}
