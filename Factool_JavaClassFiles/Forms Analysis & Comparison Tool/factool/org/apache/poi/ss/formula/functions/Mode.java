// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.TwoDEval;
import java.util.List;
import org.apache.poi.ss.formula.eval.NumberEval;
import java.util.ArrayList;
import org.apache.poi.ss.formula.eval.ValueEval;
import java.util.Arrays;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.ErrorEval;

public final class Mode implements Function
{
    public static double evaluate(final double[] v) throws EvaluationException {
        if (v.length < 2) {
            throw new EvaluationException(ErrorEval.NA);
        }
        final int[] counts = new int[v.length];
        Arrays.fill(counts, 1);
        for (int i = 0, iSize = v.length; i < iSize; ++i) {
            for (int j = i + 1, jSize = v.length; j < jSize; ++j) {
                if (v[i] == v[j]) {
                    final int[] array = counts;
                    final int n = i;
                    ++array[n];
                }
            }
        }
        double maxv = 0.0;
        int maxc = 0;
        for (int k = 0, iSize2 = counts.length; k < iSize2; ++k) {
            if (counts[k] > maxc) {
                maxv = v[k];
                maxc = counts[k];
            }
        }
        if (maxc > 1) {
            return maxv;
        }
        throw new EvaluationException(ErrorEval.NA);
    }
    
    @Override
    public ValueEval evaluate(final ValueEval[] args, final int srcCellRow, final int srcCellCol) {
        double result;
        try {
            final List<Double> temp = new ArrayList<Double>();
            for (int i = 0; i < args.length; ++i) {
                collectValues(args[i], temp);
            }
            final double[] values = new double[temp.size()];
            for (int j = 0; j < values.length; ++j) {
                values[j] = temp.get(j);
            }
            result = evaluate(values);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        return new NumberEval(result);
    }
    
    private static void collectValues(final ValueEval arg, final List<Double> temp) throws EvaluationException {
        if (arg instanceof TwoDEval) {
            final TwoDEval ae = (TwoDEval)arg;
            final int width = ae.getWidth();
            for (int height = ae.getHeight(), rrIx = 0; rrIx < height; ++rrIx) {
                for (int rcIx = 0; rcIx < width; ++rcIx) {
                    final ValueEval ve1 = ae.getValue(rrIx, rcIx);
                    collectValue(ve1, temp, false);
                }
            }
            return;
        }
        if (arg instanceof RefEval) {
            final RefEval re = (RefEval)arg;
            for (int sIx = re.getFirstSheetIndex(); sIx <= re.getLastSheetIndex(); ++sIx) {
                collectValue(re.getInnerValueEval(sIx), temp, true);
            }
            return;
        }
        collectValue(arg, temp, true);
    }
    
    private static void collectValue(final ValueEval arg, final List<Double> temp, final boolean mustBeNumber) throws EvaluationException {
        if (arg instanceof ErrorEval) {
            throw new EvaluationException((ErrorEval)arg);
        }
        if (arg == BlankEval.instance || arg instanceof BoolEval || arg instanceof StringEval) {
            if (mustBeNumber) {
                throw EvaluationException.invalidValue();
            }
        }
        else {
            if (arg instanceof NumberEval) {
                temp.add(new Double(((NumberEval)arg).getNumberValue()));
                return;
            }
            throw new RuntimeException("Unexpected value type (" + arg.getClass().getName() + ")");
        }
    }
}
