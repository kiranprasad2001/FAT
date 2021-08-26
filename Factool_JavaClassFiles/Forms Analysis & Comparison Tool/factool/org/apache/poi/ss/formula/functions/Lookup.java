// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.TwoDEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class Lookup extends Var2or3ArgFunction
{
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1) {
        throw new RuntimeException("Two arg version of LOOKUP not supported yet");
    }
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1, final ValueEval arg2) {
        try {
            final ValueEval lookupValue = OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
            final TwoDEval aeLookupVector = LookupUtils.resolveTableArrayArg(arg1);
            final TwoDEval aeResultVector = LookupUtils.resolveTableArrayArg(arg2);
            final LookupUtils.ValueVector lookupVector = createVector(aeLookupVector);
            final LookupUtils.ValueVector resultVector = createVector(aeResultVector);
            if (lookupVector.getSize() > resultVector.getSize()) {
                throw new RuntimeException("Lookup vector and result vector of differing sizes not supported yet");
            }
            final int index = LookupUtils.lookupIndexOfValue(lookupValue, lookupVector, true);
            return resultVector.getItem(index);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }
    
    private static LookupUtils.ValueVector createVector(final TwoDEval ae) {
        final LookupUtils.ValueVector result = LookupUtils.createVector(ae);
        if (result != null) {
            return result;
        }
        throw new RuntimeException("non-vector lookup or result areas not supported yet");
    }
}
