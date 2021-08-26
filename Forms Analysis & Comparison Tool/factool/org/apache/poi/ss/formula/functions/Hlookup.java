// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.TwoDEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class Hlookup extends Var3or4ArgFunction
{
    private static final ValueEval DEFAULT_ARG3;
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1, final ValueEval arg2) {
        return this.evaluate(srcRowIndex, srcColumnIndex, arg0, arg1, arg2, Hlookup.DEFAULT_ARG3);
    }
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1, final ValueEval arg2, final ValueEval arg3) {
        try {
            final ValueEval lookupValue = OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
            final TwoDEval tableArray = LookupUtils.resolveTableArrayArg(arg1);
            final boolean isRangeLookup = LookupUtils.resolveRangeLookupArg(arg3, srcRowIndex, srcColumnIndex);
            final int colIndex = LookupUtils.lookupIndexOfValue(lookupValue, LookupUtils.createRowVector(tableArray, 0), isRangeLookup);
            final int rowIndex = LookupUtils.resolveRowOrColIndexArg(arg2, srcRowIndex, srcColumnIndex);
            final LookupUtils.ValueVector resultCol = this.createResultColumnVector(tableArray, rowIndex);
            return resultCol.getItem(colIndex);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }
    
    private LookupUtils.ValueVector createResultColumnVector(final TwoDEval tableArray, final int rowIndex) throws EvaluationException {
        if (rowIndex >= tableArray.getHeight()) {
            throw EvaluationException.invalidRef();
        }
        return LookupUtils.createRowVector(tableArray, rowIndex);
    }
    
    static {
        DEFAULT_ARG3 = BoolEval.TRUE;
    }
}
