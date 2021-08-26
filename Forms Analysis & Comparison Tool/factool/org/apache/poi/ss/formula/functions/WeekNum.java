// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.OperationEvaluationContext;
import java.util.Calendar;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.usermodel.DateUtil;
import java.util.GregorianCalendar;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.ValueEval;

public class WeekNum extends Fixed2ArgFunction implements FreeRefFunction
{
    public static final FreeRefFunction instance;
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval serialNumVE, final ValueEval returnTypeVE) {
        double serialNum = 0.0;
        try {
            serialNum = NumericFunction.singleOperandEvaluate(serialNumVE, srcRowIndex, srcColumnIndex);
        }
        catch (EvaluationException e) {
            return ErrorEval.VALUE_INVALID;
        }
        final Calendar serialNumCalendar = new GregorianCalendar();
        serialNumCalendar.setTime(DateUtil.getJavaDate(serialNum, false));
        int returnType = 0;
        try {
            final ValueEval ve = OperandResolver.getSingleValue(returnTypeVE, srcRowIndex, srcColumnIndex);
            returnType = OperandResolver.coerceValueToInt(ve);
        }
        catch (EvaluationException e2) {
            return ErrorEval.NUM_ERROR;
        }
        if (returnType != 1 && returnType != 2) {
            return ErrorEval.NUM_ERROR;
        }
        return new NumberEval(this.getWeekNo(serialNumCalendar, returnType));
    }
    
    public int getWeekNo(final Calendar cal, final int weekStartOn) {
        if (weekStartOn == 1) {
            cal.setFirstDayOfWeek(1);
        }
        else {
            cal.setFirstDayOfWeek(2);
        }
        return cal.get(3);
    }
    
    @Override
    public ValueEval evaluate(final ValueEval[] args, final OperationEvaluationContext ec) {
        if (args.length == 2) {
            return this.evaluate(ec.getRowIndex(), ec.getColumnIndex(), args[0], args[1]);
        }
        return ErrorEval.VALUE_INVALID;
    }
    
    static {
        instance = new WeekNum();
    }
}
