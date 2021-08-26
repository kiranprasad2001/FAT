// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.TwoDEval;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class Counta implements Function
{
    private final CountUtils.I_MatchPredicate _predicate;
    private static final CountUtils.I_MatchPredicate defaultPredicate;
    private static final CountUtils.I_MatchPredicate subtotalPredicate;
    
    public Counta() {
        this._predicate = Counta.defaultPredicate;
    }
    
    private Counta(final CountUtils.I_MatchPredicate criteriaPredicate) {
        this._predicate = criteriaPredicate;
    }
    
    @Override
    public ValueEval evaluate(final ValueEval[] args, final int srcCellRow, final int srcCellCol) {
        final int nArgs = args.length;
        if (nArgs < 1) {
            return ErrorEval.VALUE_INVALID;
        }
        if (nArgs > 30) {
            return ErrorEval.VALUE_INVALID;
        }
        int temp = 0;
        for (int i = 0; i < nArgs; ++i) {
            temp += CountUtils.countArg(args[i], this._predicate);
        }
        return new NumberEval(temp);
    }
    
    public static Counta subtotalInstance() {
        return new Counta(Counta.subtotalPredicate);
    }
    
    static {
        defaultPredicate = new CountUtils.I_MatchPredicate() {
            @Override
            public boolean matches(final ValueEval valueEval) {
                return valueEval != BlankEval.instance;
            }
        };
        subtotalPredicate = new CountUtils.I_MatchAreaPredicate() {
            @Override
            public boolean matches(final ValueEval valueEval) {
                return Counta.defaultPredicate.matches(valueEval);
            }
            
            @Override
            public boolean matches(final TwoDEval areEval, final int rowIndex, final int columnIndex) {
                return !areEval.isSubTotal(rowIndex, columnIndex);
            }
        };
    }
}
