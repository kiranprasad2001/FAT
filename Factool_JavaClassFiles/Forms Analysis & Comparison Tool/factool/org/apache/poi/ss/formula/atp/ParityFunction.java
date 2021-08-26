// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.atp;

import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.FreeRefFunction;

final class ParityFunction implements FreeRefFunction
{
    public static final FreeRefFunction IS_EVEN;
    public static final FreeRefFunction IS_ODD;
    private final int _desiredParity;
    
    private ParityFunction(final int desiredParity) {
        this._desiredParity = desiredParity;
    }
    
    @Override
    public ValueEval evaluate(final ValueEval[] args, final OperationEvaluationContext ec) {
        if (args.length != 1) {
            return ErrorEval.VALUE_INVALID;
        }
        int val;
        try {
            val = evaluateArgParity(args[0], ec.getRowIndex(), ec.getColumnIndex());
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        return BoolEval.valueOf(val == this._desiredParity);
    }
    
    private static int evaluateArgParity(final ValueEval arg, final int srcCellRow, final int srcCellCol) throws EvaluationException {
        final ValueEval ve = OperandResolver.getSingleValue(arg, srcCellRow, (short)srcCellCol);
        double d = OperandResolver.coerceValueToDouble(ve);
        if (d < 0.0) {
            d = -d;
        }
        final long v = (long)Math.floor(d);
        return (int)(v & 0x1L);
    }
    
    static {
        IS_EVEN = new ParityFunction(0);
        IS_ODD = new ParityFunction(1);
    }
}
