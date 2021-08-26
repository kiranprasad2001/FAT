// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import java.util.Calendar;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.MissingArgEval;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class WeekdayFunc implements Function
{
    public static final Function instance;
    
    private WeekdayFunc() {
    }
    
    @Override
    public ValueEval evaluate(final ValueEval[] args, final int srcRowIndex, final int srcColumnIndex) {
        try {
            if (args.length < 1 || args.length > 2) {
                return ErrorEval.VALUE_INVALID;
            }
            final ValueEval serialDateVE = OperandResolver.getSingleValue(args[0], srcRowIndex, srcColumnIndex);
            final double serialDate = OperandResolver.coerceValueToDouble(serialDateVE);
            if (!DateUtil.isValidExcelDate(serialDate)) {
                return ErrorEval.NUM_ERROR;
            }
            final Calendar date = DateUtil.getJavaCalendar(serialDate, false);
            final int weekday = date.get(7);
            int returnOption = 1;
            if (args.length == 2) {
                final ValueEval ve = OperandResolver.getSingleValue(args[1], srcRowIndex, srcColumnIndex);
                if (ve == MissingArgEval.instance || ve == BlankEval.instance) {
                    return ErrorEval.NUM_ERROR;
                }
                returnOption = OperandResolver.coerceValueToInt(ve);
                if (returnOption == 2) {
                    returnOption = 11;
                }
            }
            double result;
            if (returnOption == 1) {
                result = weekday;
            }
            else if (returnOption == 3) {
                result = (weekday + 6 - 1) % 7;
            }
            else {
                if (returnOption < 11 || returnOption > 17) {
                    return ErrorEval.NUM_ERROR;
                }
                result = (weekday + 6 - (returnOption - 10)) % 7 + 1;
            }
            return new NumberEval(result);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }
    
    static {
        instance = new WeekdayFunc();
    }
}
