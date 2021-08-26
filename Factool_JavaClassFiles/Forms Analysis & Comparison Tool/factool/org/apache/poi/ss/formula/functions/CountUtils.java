// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.TwoDEval;
import org.apache.poi.ss.formula.ThreeDEval;

final class CountUtils
{
    private CountUtils() {
    }
    
    public static int countMatchingCellsInArea(final ThreeDEval areaEval, final I_MatchPredicate criteriaPredicate) {
        int result = 0;
        for (int sIx = areaEval.getFirstSheetIndex(); sIx <= areaEval.getLastSheetIndex(); ++sIx) {
            final int height = areaEval.getHeight();
            final int width = areaEval.getWidth();
            for (int rrIx = 0; rrIx < height; ++rrIx) {
                for (int rcIx = 0; rcIx < width; ++rcIx) {
                    final ValueEval ve = areaEval.getValue(sIx, rrIx, rcIx);
                    if (criteriaPredicate instanceof I_MatchAreaPredicate) {
                        final I_MatchAreaPredicate areaPredicate = (I_MatchAreaPredicate)criteriaPredicate;
                        if (!areaPredicate.matches(areaEval, rrIx, rcIx)) {
                            continue;
                        }
                    }
                    if (criteriaPredicate.matches(ve)) {
                        ++result;
                    }
                }
            }
        }
        return result;
    }
    
    public static int countMatchingCellsInRef(final RefEval refEval, final I_MatchPredicate criteriaPredicate) {
        int result = 0;
        for (int sIx = refEval.getFirstSheetIndex(); sIx <= refEval.getLastSheetIndex(); ++sIx) {
            final ValueEval ve = refEval.getInnerValueEval(sIx);
            if (criteriaPredicate.matches(ve)) {
                ++result;
            }
        }
        return result;
    }
    
    public static int countArg(final ValueEval eval, final I_MatchPredicate criteriaPredicate) {
        if (eval == null) {
            throw new IllegalArgumentException("eval must not be null");
        }
        if (eval instanceof ThreeDEval) {
            return countMatchingCellsInArea((ThreeDEval)eval, criteriaPredicate);
        }
        if (eval instanceof TwoDEval) {
            throw new IllegalArgumentException("Count requires 3D Evals, 2D ones aren't supported");
        }
        if (eval instanceof RefEval) {
            return countMatchingCellsInRef((RefEval)eval, criteriaPredicate);
        }
        return criteriaPredicate.matches(eval) ? 1 : 0;
    }
    
    public interface I_MatchAreaPredicate extends I_MatchPredicate
    {
        boolean matches(final TwoDEval p0, final int p1, final int p2);
    }
    
    public interface I_MatchPredicate
    {
        boolean matches(final ValueEval p0);
    }
}
