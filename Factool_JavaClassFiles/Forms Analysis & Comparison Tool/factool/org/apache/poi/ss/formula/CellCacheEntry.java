// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula;

import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.ValueEval;

abstract class CellCacheEntry implements IEvaluationListener.ICacheEntry
{
    public static final CellCacheEntry[] EMPTY_ARRAY;
    private final FormulaCellCacheEntrySet _consumingCells;
    private ValueEval _value;
    
    protected CellCacheEntry() {
        this._consumingCells = new FormulaCellCacheEntrySet();
    }
    
    protected final void clearValue() {
        this._value = null;
    }
    
    public final boolean updateValue(final ValueEval value) {
        if (value == null) {
            throw new IllegalArgumentException("Did not expect to update to null");
        }
        final boolean result = !areValuesEqual(this._value, value);
        this._value = value;
        return result;
    }
    
    @Override
    public final ValueEval getValue() {
        return this._value;
    }
    
    private static boolean areValuesEqual(final ValueEval a, final ValueEval b) {
        if (a == null) {
            return false;
        }
        final Class<? extends ValueEval> cls = a.getClass();
        if (cls != b.getClass()) {
            return false;
        }
        if (a == BlankEval.instance) {
            return b == a;
        }
        if (cls == NumberEval.class) {
            return ((NumberEval)a).getNumberValue() == ((NumberEval)b).getNumberValue();
        }
        if (cls == StringEval.class) {
            return ((StringEval)a).getStringValue().equals(((StringEval)b).getStringValue());
        }
        if (cls == BoolEval.class) {
            return ((BoolEval)a).getBooleanValue() == ((BoolEval)b).getBooleanValue();
        }
        if (cls == ErrorEval.class) {
            return ((ErrorEval)a).getErrorCode() == ((ErrorEval)b).getErrorCode();
        }
        throw new IllegalStateException("Unexpected value class (" + cls.getName() + ")");
    }
    
    public final void addConsumingCell(final FormulaCellCacheEntry cellLoc) {
        this._consumingCells.add(cellLoc);
    }
    
    public final FormulaCellCacheEntry[] getConsumingCells() {
        return this._consumingCells.toArray();
    }
    
    public final void clearConsumingCell(final FormulaCellCacheEntry cce) {
        if (!this._consumingCells.remove(cce)) {
            throw new IllegalStateException("Specified formula cell is not consumed by this cell");
        }
    }
    
    public final void recurseClearCachedFormulaResults(final IEvaluationListener listener) {
        if (listener == null) {
            this.recurseClearCachedFormulaResults();
        }
        else {
            listener.onClearCachedValue(this);
            this.recurseClearCachedFormulaResults(listener, 1);
        }
    }
    
    protected final void recurseClearCachedFormulaResults() {
        final FormulaCellCacheEntry[] formulaCells = this.getConsumingCells();
        for (int i = 0; i < formulaCells.length; ++i) {
            final FormulaCellCacheEntry fc = formulaCells[i];
            fc.clearFormulaEntry();
            fc.recurseClearCachedFormulaResults();
        }
    }
    
    protected final void recurseClearCachedFormulaResults(final IEvaluationListener listener, final int depth) {
        final FormulaCellCacheEntry[] formulaCells = this.getConsumingCells();
        listener.sortDependentCachedValues(formulaCells);
        for (int i = 0; i < formulaCells.length; ++i) {
            final FormulaCellCacheEntry fc = formulaCells[i];
            listener.onClearDependentCachedValue(fc, depth);
            fc.clearFormulaEntry();
            fc.recurseClearCachedFormulaResults(listener, depth + 1);
        }
    }
    
    static {
        EMPTY_ARRAY = new CellCacheEntry[0];
    }
}
