// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.ThreeDEval;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class Countblank extends Fixed1ArgFunction
{
    private static final CountUtils.I_MatchPredicate predicate;
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0) {
        double result;
        if (arg0 instanceof RefEval) {
            result = CountUtils.countMatchingCellsInRef((RefEval)arg0, Countblank.predicate);
        }
        else {
            if (!(arg0 instanceof ThreeDEval)) {
                throw new IllegalArgumentException("Bad range arg type (" + arg0.getClass().getName() + ")");
            }
            result = CountUtils.countMatchingCellsInArea((ThreeDEval)arg0, Countblank.predicate);
        }
        return new NumberEval(result);
    }
    
    static {
        predicate = new CountUtils.I_MatchPredicate() {
            @Override
            public boolean matches(final ValueEval valueEval) {
                return valueEval == BlankEval.instance;
            }
        };
    }
}
