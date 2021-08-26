// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.AreaEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.TwoDEval;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.NumericValueEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class Sumproduct implements Function
{
    @Override
    public ValueEval evaluate(final ValueEval[] args, final int srcCellRow, final int srcCellCol) {
        final int maxN = args.length;
        if (maxN < 1) {
            return ErrorEval.VALUE_INVALID;
        }
        final ValueEval firstArg = args[0];
        try {
            if (firstArg instanceof NumericValueEval) {
                return evaluateSingleProduct(args);
            }
            if (firstArg instanceof RefEval) {
                return evaluateSingleProduct(args);
            }
            if (firstArg instanceof TwoDEval) {
                final TwoDEval ae = (TwoDEval)firstArg;
                if (ae.isRow() && ae.isColumn()) {
                    return evaluateSingleProduct(args);
                }
                return evaluateAreaSumProduct(args);
            }
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        throw new RuntimeException("Invalid arg type for SUMPRODUCT: (" + firstArg.getClass().getName() + ")");
    }
    
    private static ValueEval evaluateSingleProduct(final ValueEval[] evalArgs) throws EvaluationException {
        final int maxN = evalArgs.length;
        double term = 1.0;
        for (int n = 0; n < maxN; ++n) {
            final double val = getScalarValue(evalArgs[n]);
            term *= val;
        }
        return new NumberEval(term);
    }
    
    private static double getScalarValue(final ValueEval arg) throws EvaluationException {
        ValueEval eval;
        if (arg instanceof RefEval) {
            final RefEval re = (RefEval)arg;
            if (re.getNumberOfSheets() > 1) {
                throw new EvaluationException(ErrorEval.VALUE_INVALID);
            }
            eval = re.getInnerValueEval(re.getFirstSheetIndex());
        }
        else {
            eval = arg;
        }
        if (eval == null) {
            throw new RuntimeException("parameter may not be null");
        }
        if (eval instanceof AreaEval) {
            final AreaEval ae = (AreaEval)eval;
            if (!ae.isColumn() || !ae.isRow()) {
                throw new EvaluationException(ErrorEval.VALUE_INVALID);
            }
            eval = ae.getRelativeValue(0, 0);
        }
        return getProductTerm(eval, true);
    }
    
    private static ValueEval evaluateAreaSumProduct(final ValueEval[] evalArgs) throws EvaluationException {
        final int maxN = evalArgs.length;
        final TwoDEval[] args = new TwoDEval[maxN];
        try {
            System.arraycopy(evalArgs, 0, args, 0, maxN);
        }
        catch (ArrayStoreException e) {
            return ErrorEval.VALUE_INVALID;
        }
        final TwoDEval firstArg = args[0];
        final int height = firstArg.getHeight();
        final int width = firstArg.getWidth();
        if (!areasAllSameSize(args, height, width)) {
            for (int i = 1; i < args.length; ++i) {
                throwFirstError(args[i]);
            }
            return ErrorEval.VALUE_INVALID;
        }
        double acc = 0.0;
        for (int rrIx = 0; rrIx < height; ++rrIx) {
            for (int rcIx = 0; rcIx < width; ++rcIx) {
                double term = 1.0;
                for (int n = 0; n < maxN; ++n) {
                    final double val = getProductTerm(args[n].getValue(rrIx, rcIx), false);
                    term *= val;
                }
                acc += term;
            }
        }
        return new NumberEval(acc);
    }
    
    private static void throwFirstError(final TwoDEval areaEval) throws EvaluationException {
        final int height = areaEval.getHeight();
        final int width = areaEval.getWidth();
        for (int rrIx = 0; rrIx < height; ++rrIx) {
            for (int rcIx = 0; rcIx < width; ++rcIx) {
                final ValueEval ve = areaEval.getValue(rrIx, rcIx);
                if (ve instanceof ErrorEval) {
                    throw new EvaluationException((ErrorEval)ve);
                }
            }
        }
    }
    
    private static boolean areasAllSameSize(final TwoDEval[] args, final int height, final int width) {
        for (int i = 0; i < args.length; ++i) {
            final TwoDEval areaEval = args[i];
            if (areaEval.getHeight() != height) {
                return false;
            }
            if (areaEval.getWidth() != width) {
                return false;
            }
        }
        return true;
    }
    
    private static double getProductTerm(final ValueEval ve, final boolean isScalarProduct) throws EvaluationException {
        if (ve instanceof BlankEval || ve == null) {
            if (isScalarProduct) {
                throw new EvaluationException(ErrorEval.VALUE_INVALID);
            }
            return 0.0;
        }
        else {
            if (ve instanceof ErrorEval) {
                throw new EvaluationException((ErrorEval)ve);
            }
            if (ve instanceof StringEval) {
                if (isScalarProduct) {
                    throw new EvaluationException(ErrorEval.VALUE_INVALID);
                }
                return 0.0;
            }
            else {
                if (ve instanceof NumericValueEval) {
                    final NumericValueEval nve = (NumericValueEval)ve;
                    return nve.getNumberValue();
                }
                throw new RuntimeException("Unexpected value eval class (" + ve.getClass().getName() + ")");
            }
        }
    }
}
