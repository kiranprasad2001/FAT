// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.atp;

import org.apache.poi.ss.formula.WorkbookEvaluator;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.FreeRefFunction;

final class IfError implements FreeRefFunction
{
    public static final FreeRefFunction instance;
    
    private IfError() {
    }
    
    @Override
    public ValueEval evaluate(final ValueEval[] args, final OperationEvaluationContext ec) {
        if (args.length != 2) {
            return ErrorEval.VALUE_INVALID;
        }
        ValueEval val;
        try {
            val = evaluateInternal(args[0], args[1], ec.getRowIndex(), ec.getColumnIndex());
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        return val;
    }
    
    private static ValueEval evaluateInternal(ValueEval arg, final ValueEval iferror, final int srcCellRow, final int srcCellCol) throws EvaluationException {
        arg = WorkbookEvaluator.dereferenceResult(arg, srcCellRow, srcCellCol);
        if (arg instanceof ErrorEval) {
            return iferror;
        }
        return arg;
    }
    
    static {
        instance = new IfError();
    }
}
