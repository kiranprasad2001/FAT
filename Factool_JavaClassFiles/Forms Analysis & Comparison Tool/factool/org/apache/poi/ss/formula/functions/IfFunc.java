// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.MissingArgEval;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class IfFunc extends Var2or3ArgFunction
{
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1) {
        boolean b;
        try {
            b = evaluateFirstArg(arg0, srcRowIndex, srcColumnIndex);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        if (!b) {
            return BoolEval.FALSE;
        }
        if (arg1 == MissingArgEval.instance) {
            return BlankEval.instance;
        }
        return arg1;
    }
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1, final ValueEval arg2) {
        boolean b;
        try {
            b = evaluateFirstArg(arg0, srcRowIndex, srcColumnIndex);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        if (b) {
            if (arg1 == MissingArgEval.instance) {
                return BlankEval.instance;
            }
            return arg1;
        }
        else {
            if (arg2 == MissingArgEval.instance) {
                return BlankEval.instance;
            }
            return arg2;
        }
    }
    
    public static boolean evaluateFirstArg(final ValueEval arg, final int srcCellRow, final int srcCellCol) throws EvaluationException {
        final ValueEval ve = OperandResolver.getSingleValue(arg, srcCellRow, srcCellCol);
        final Boolean b = OperandResolver.coerceValueToBoolean(ve, false);
        return b != null && b;
    }
}
