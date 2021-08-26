// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import java.util.Calendar;
import java.util.Date;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import java.util.GregorianCalendar;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ValueEval;

public class EOMonth implements FreeRefFunction
{
    public static final FreeRefFunction instance;
    
    @Override
    public ValueEval evaluate(final ValueEval[] args, final OperationEvaluationContext ec) {
        if (args.length != 2) {
            return ErrorEval.VALUE_INVALID;
        }
        try {
            double startDateAsNumber = NumericFunction.singleOperandEvaluate(args[0], ec.getRowIndex(), ec.getColumnIndex());
            final int months = (int)NumericFunction.singleOperandEvaluate(args[1], ec.getRowIndex(), ec.getColumnIndex());
            if (startDateAsNumber >= 0.0 && startDateAsNumber < 1.0) {
                startDateAsNumber = 1.0;
            }
            final Date startDate = DateUtil.getJavaDate(startDateAsNumber, false);
            final Calendar cal = new GregorianCalendar();
            cal.setTime(startDate);
            cal.set(cal.get(1), cal.get(2), cal.get(5), 0, 0, 0);
            cal.set(14, 0);
            cal.add(2, months + 1);
            cal.set(5, 1);
            cal.add(5, -1);
            return new NumberEval(DateUtil.getExcelDate(cal.getTime()));
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }
    
    static {
        instance = new EOMonth();
    }
}
