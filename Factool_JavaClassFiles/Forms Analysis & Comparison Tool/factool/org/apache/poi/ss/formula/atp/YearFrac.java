// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.atp;

import java.util.Calendar;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.FreeRefFunction;

final class YearFrac implements FreeRefFunction
{
    public static final FreeRefFunction instance;
    
    private YearFrac() {
    }
    
    @Override
    public ValueEval evaluate(final ValueEval[] args, final OperationEvaluationContext ec) {
        final int srcCellRow = ec.getRowIndex();
        final int srcCellCol = ec.getColumnIndex();
        double result = 0.0;
        try {
            int basis = 0;
            switch (args.length) {
                case 3: {
                    basis = evaluateIntArg(args[2], srcCellRow, srcCellCol);
                }
                case 2: {
                    final double startDateVal = evaluateDateArg(args[0], srcCellRow, srcCellCol);
                    final double endDateVal = evaluateDateArg(args[1], srcCellRow, srcCellCol);
                    result = YearFracCalculator.calculate(startDateVal, endDateVal, basis);
                    break;
                }
                default: {
                    return ErrorEval.VALUE_INVALID;
                }
            }
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        return new NumberEval(result);
    }
    
    private static double evaluateDateArg(final ValueEval arg, final int srcCellRow, final int srcCellCol) throws EvaluationException {
        final ValueEval ve = OperandResolver.getSingleValue(arg, srcCellRow, (short)srcCellCol);
        if (!(ve instanceof StringEval)) {
            return OperandResolver.coerceValueToDouble(ve);
        }
        final String strVal = ((StringEval)ve).getStringValue();
        final Double dVal = OperandResolver.parseDouble(strVal);
        if (dVal != null) {
            return dVal;
        }
        final Calendar date = DateParser.parseDate(strVal);
        return DateUtil.getExcelDate(date, false);
    }
    
    private static int evaluateIntArg(final ValueEval arg, final int srcCellRow, final int srcCellCol) throws EvaluationException {
        final ValueEval ve = OperandResolver.getSingleValue(arg, srcCellRow, (short)srcCellCol);
        return OperandResolver.coerceValueToInt(ve);
    }
    
    static {
        instance = new YearFrac();
    }
}
