// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.NumericValueEval;
import org.apache.poi.ss.formula.TwoDEval;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class Match extends Var2or3ArgFunction
{
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1) {
        return eval(srcRowIndex, srcColumnIndex, arg0, arg1, 1.0);
    }
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1, final ValueEval arg2) {
        double match_type;
        try {
            match_type = evaluateMatchTypeArg(arg2, srcRowIndex, srcColumnIndex);
        }
        catch (EvaluationException e) {
            return ErrorEval.REF_INVALID;
        }
        return eval(srcRowIndex, srcColumnIndex, arg0, arg1, match_type);
    }
    
    private static ValueEval eval(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1, final double match_type) {
        final boolean matchExact = match_type == 0.0;
        final boolean findLargestLessThanOrEqual = match_type > 0.0;
        try {
            final ValueEval lookupValue = OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
            final LookupUtils.ValueVector lookupRange = evaluateLookupRange(arg1);
            final int index = findIndexOfValue(lookupValue, lookupRange, matchExact, findLargestLessThanOrEqual);
            return new NumberEval(index + 1);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }
    
    private static LookupUtils.ValueVector evaluateLookupRange(final ValueEval eval) throws EvaluationException {
        if (eval instanceof RefEval) {
            final RefEval re = (RefEval)eval;
            if (re.getNumberOfSheets() == 1) {
                return new SingleValueVector(re.getInnerValueEval(re.getFirstSheetIndex()));
            }
            return LookupUtils.createVector(re);
        }
        else if (eval instanceof TwoDEval) {
            final LookupUtils.ValueVector result = LookupUtils.createVector((TwoDEval)eval);
            if (result == null) {
                throw new EvaluationException(ErrorEval.NA);
            }
            return result;
        }
        else {
            if (eval instanceof NumericValueEval) {
                throw new EvaluationException(ErrorEval.NA);
            }
            if (!(eval instanceof StringEval)) {
                throw new RuntimeException("Unexpected eval type (" + eval.getClass().getName() + ")");
            }
            final StringEval se = (StringEval)eval;
            final Double d = OperandResolver.parseDouble(se.getStringValue());
            if (d == null) {
                throw new EvaluationException(ErrorEval.VALUE_INVALID);
            }
            throw new EvaluationException(ErrorEval.NA);
        }
    }
    
    private static double evaluateMatchTypeArg(final ValueEval arg, final int srcCellRow, final int srcCellCol) throws EvaluationException {
        final ValueEval match_type = OperandResolver.getSingleValue(arg, srcCellRow, srcCellCol);
        if (match_type instanceof ErrorEval) {
            throw new EvaluationException((ErrorEval)match_type);
        }
        if (match_type instanceof NumericValueEval) {
            final NumericValueEval ne = (NumericValueEval)match_type;
            return ne.getNumberValue();
        }
        if (!(match_type instanceof StringEval)) {
            throw new RuntimeException("Unexpected match_type type (" + match_type.getClass().getName() + ")");
        }
        final StringEval se = (StringEval)match_type;
        final Double d = OperandResolver.parseDouble(se.getStringValue());
        if (d == null) {
            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        }
        return d;
    }
    
    private static int findIndexOfValue(final ValueEval lookupValue, final LookupUtils.ValueVector lookupRange, final boolean matchExact, final boolean findLargestLessThanOrEqual) throws EvaluationException {
        final LookupUtils.LookupValueComparer lookupComparer = createLookupComparer(lookupValue, matchExact);
        final int size = lookupRange.getSize();
        if (matchExact) {
            for (int i = 0; i < size; ++i) {
                if (lookupComparer.compareTo(lookupRange.getItem(i)).isEqual()) {
                    return i;
                }
            }
            throw new EvaluationException(ErrorEval.NA);
        }
        if (findLargestLessThanOrEqual) {
            for (int i = size - 1; i >= 0; --i) {
                final LookupUtils.CompareResult cmp = lookupComparer.compareTo(lookupRange.getItem(i));
                if (!cmp.isTypeMismatch()) {
                    if (!cmp.isLessThan()) {
                        return i;
                    }
                }
            }
            throw new EvaluationException(ErrorEval.NA);
        }
        int i = 0;
        while (i < size) {
            final LookupUtils.CompareResult cmp = lookupComparer.compareTo(lookupRange.getItem(i));
            if (cmp.isEqual()) {
                return i;
            }
            if (cmp.isGreaterThan()) {
                if (i < 1) {
                    throw new EvaluationException(ErrorEval.NA);
                }
                return i - 1;
            }
            else {
                ++i;
            }
        }
        throw new EvaluationException(ErrorEval.NA);
    }
    
    private static LookupUtils.LookupValueComparer createLookupComparer(final ValueEval lookupValue, final boolean matchExact) {
        return LookupUtils.createLookupComparer(lookupValue, matchExact, true);
    }
    
    private static boolean isLookupValueWild(final String stringValue) {
        return stringValue.indexOf(63) >= 0 || stringValue.indexOf(42) >= 0;
    }
    
    private static final class SingleValueVector implements LookupUtils.ValueVector
    {
        private final ValueEval _value;
        
        public SingleValueVector(final ValueEval value) {
            this._value = value;
        }
        
        @Override
        public ValueEval getItem(final int index) {
            if (index != 0) {
                throw new RuntimeException("Invalid index (" + index + ") only zero is allowed");
            }
            return this._value;
        }
        
        @Override
        public int getSize() {
            return 1;
        }
    }
}
