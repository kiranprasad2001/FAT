// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.TwoDEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class Vlookup extends Var3or4ArgFunction
{
    private static final ValueEval DEFAULT_ARG3;
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1, final ValueEval arg2) {
        return this.evaluate(srcRowIndex, srcColumnIndex, arg0, arg1, arg2, Vlookup.DEFAULT_ARG3);
    }
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval lookup_value, final ValueEval table_array, final ValueEval col_index, final ValueEval range_lookup) {
        try {
            final ValueEval lookupValue = OperandResolver.getSingleValue(lookup_value, srcRowIndex, srcColumnIndex);
            final TwoDEval tableArray = LookupUtils.resolveTableArrayArg(table_array);
            final boolean isRangeLookup = LookupUtils.resolveRangeLookupArg(range_lookup, srcRowIndex, srcColumnIndex);
            final int rowIndex = LookupUtils.lookupIndexOfValue(lookupValue, LookupUtils.createColumnVector(tableArray, 0), isRangeLookup);
            final int colIndex = LookupUtils.resolveRowOrColIndexArg(col_index, srcRowIndex, srcColumnIndex);
            final LookupUtils.ValueVector resultCol = this.createResultColumnVector(tableArray, colIndex);
            return resultCol.getItem(rowIndex);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }
    
    private LookupUtils.ValueVector createResultColumnVector(final TwoDEval tableArray, final int colIndex) throws EvaluationException {
        if (colIndex >= tableArray.getWidth()) {
            throw EvaluationException.invalidRef();
        }
        return LookupUtils.createColumnVector(tableArray, colIndex);
    }
    
    static {
        DEFAULT_ARG3 = BoolEval.TRUE;
    }
}
