// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.atp;

import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.FreeRefFunction;

final class NetworkdaysFunction implements FreeRefFunction
{
    public static final FreeRefFunction instance;
    private ArgumentsEvaluator evaluator;
    
    private NetworkdaysFunction(final ArgumentsEvaluator anEvaluator) {
        this.evaluator = anEvaluator;
    }
    
    @Override
    public ValueEval evaluate(final ValueEval[] args, final OperationEvaluationContext ec) {
        if (args.length < 2 || args.length > 3) {
            return ErrorEval.VALUE_INVALID;
        }
        final int srcCellRow = ec.getRowIndex();
        final int srcCellCol = ec.getColumnIndex();
        try {
            final double start = this.evaluator.evaluateDateArg(args[0], srcCellRow, srcCellCol);
            final double end = this.evaluator.evaluateDateArg(args[1], srcCellRow, srcCellCol);
            if (start > end) {
                return ErrorEval.NAME_INVALID;
            }
            final ValueEval holidaysCell = (args.length == 3) ? args[2] : null;
            final double[] holidays = this.evaluator.evaluateDatesArg(holidaysCell, srcCellRow, srcCellCol);
            return new NumberEval(WorkdayCalculator.instance.calculateWorkdays(start, end, holidays));
        }
        catch (EvaluationException e) {
            return ErrorEval.VALUE_INVALID;
        }
    }
    
    static {
        instance = new NetworkdaysFunction(ArgumentsEvaluator.instance);
    }
}
