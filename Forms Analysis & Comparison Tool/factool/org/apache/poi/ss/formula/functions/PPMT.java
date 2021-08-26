// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.ValueEval;

public class PPMT extends NumericFunction
{
    public double eval(final ValueEval[] args, final int srcCellRow, final int srcCellCol) throws EvaluationException {
        if (args.length < 4) {
            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        }
        final ValueEval v1 = OperandResolver.getSingleValue(args[0], srcCellRow, srcCellCol);
        final ValueEval v2 = OperandResolver.getSingleValue(args[1], srcCellRow, srcCellCol);
        final ValueEval v3 = OperandResolver.getSingleValue(args[2], srcCellRow, srcCellCol);
        final ValueEval v4 = OperandResolver.getSingleValue(args[3], srcCellRow, srcCellCol);
        final double interestRate = OperandResolver.coerceValueToDouble(v1);
        final int period = OperandResolver.coerceValueToInt(v2);
        final int numberPayments = OperandResolver.coerceValueToInt(v3);
        final double PV = OperandResolver.coerceValueToDouble(v4);
        final double result = Finance.ppmt(interestRate, period, numberPayments, PV);
        NumericFunction.checkValue(result);
        return result;
    }
}
