// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.BlankEval;
import java.util.Date;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import java.util.Calendar;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ValueEval;

public class EDate implements FreeRefFunction
{
    public static final FreeRefFunction instance;
    
    @Override
    public ValueEval evaluate(final ValueEval[] args, final OperationEvaluationContext ec) {
        if (args.length != 2) {
            return ErrorEval.VALUE_INVALID;
        }
        try {
            final double startDateAsNumber = this.getValue(args[0]);
            final int offsetInMonthAsNumber = (int)this.getValue(args[1]);
            final Date startDate = DateUtil.getJavaDate(startDateAsNumber);
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            calendar.add(2, offsetInMonthAsNumber);
            return new NumberEval(DateUtil.getExcelDate(calendar.getTime()));
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }
    
    private double getValue(final ValueEval arg) throws EvaluationException {
        if (arg instanceof NumberEval) {
            return ((NumberEval)arg).getNumberValue();
        }
        if (arg instanceof BlankEval) {
            return 0.0;
        }
        if (arg instanceof RefEval) {
            final RefEval refEval = (RefEval)arg;
            if (refEval.getNumberOfSheets() > 1) {
                throw new EvaluationException(ErrorEval.VALUE_INVALID);
            }
            final ValueEval innerValueEval = refEval.getInnerValueEval(refEval.getFirstSheetIndex());
            if (innerValueEval instanceof NumberEval) {
                return ((NumberEval)innerValueEval).getNumberValue();
            }
            if (innerValueEval instanceof BlankEval) {
                return 0.0;
            }
        }
        throw new EvaluationException(ErrorEval.VALUE_INVALID);
    }
    
    static {
        instance = new EDate();
    }
}
