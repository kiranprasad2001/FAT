// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula;

import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.eval.BlankEval;

final class EvaluationCache
{
    private final PlainCellCache _plainCellCache;
    private final FormulaCellCache _formulaCellCache;
    final IEvaluationListener _evaluationListener;
    
    EvaluationCache(final IEvaluationListener evaluationListener) {
        this._evaluationListener = evaluationListener;
        this._plainCellCache = new PlainCellCache();
        this._formulaCellCache = new FormulaCellCache();
    }
    
    public void notifyUpdateCell(final int bookIndex, final int sheetIndex, final EvaluationCell cell) {
        FormulaCellCacheEntry fcce = this._formulaCellCache.get(cell);
        final int rowIndex = cell.getRowIndex();
        final int columnIndex = cell.getColumnIndex();
        final PlainCellCache.Loc loc = new PlainCellCache.Loc(bookIndex, sheetIndex, rowIndex, columnIndex);
        PlainValueCellCacheEntry pcce = this._plainCellCache.get(loc);
        if (cell.getCellType() == 2) {
            if (fcce == null) {
                fcce = new FormulaCellCacheEntry();
                if (pcce == null) {
                    if (this._evaluationListener != null) {
                        this._evaluationListener.onChangeFromBlankValue(sheetIndex, rowIndex, columnIndex, cell, fcce);
                    }
                    this.updateAnyBlankReferencingFormulas(bookIndex, sheetIndex, rowIndex, columnIndex);
                }
                this._formulaCellCache.put(cell, fcce);
            }
            else {
                fcce.recurseClearCachedFormulaResults(this._evaluationListener);
                fcce.clearFormulaEntry();
            }
            if (pcce != null) {
                pcce.recurseClearCachedFormulaResults(this._evaluationListener);
                this._plainCellCache.remove(loc);
            }
        }
        else {
            final ValueEval value = WorkbookEvaluator.getValueFromNonFormulaCell(cell);
            if (pcce == null) {
                if (value != BlankEval.instance) {
                    pcce = new PlainValueCellCacheEntry(value);
                    if (fcce == null) {
                        if (this._evaluationListener != null) {
                            this._evaluationListener.onChangeFromBlankValue(sheetIndex, rowIndex, columnIndex, cell, pcce);
                        }
                        this.updateAnyBlankReferencingFormulas(bookIndex, sheetIndex, rowIndex, columnIndex);
                    }
                    this._plainCellCache.put(loc, pcce);
                }
            }
            else {
                if (pcce.updateValue(value)) {
                    pcce.recurseClearCachedFormulaResults(this._evaluationListener);
                }
                if (value == BlankEval.instance) {
                    this._plainCellCache.remove(loc);
                }
            }
            if (fcce != null) {
                this._formulaCellCache.remove(cell);
                fcce.setSensitiveInputCells(null);
                fcce.recurseClearCachedFormulaResults(this._evaluationListener);
            }
        }
    }
    
    private void updateAnyBlankReferencingFormulas(final int bookIndex, final int sheetIndex, final int rowIndex, final int columnIndex) {
        final FormulaUsedBlankCellSet.BookSheetKey bsk = new FormulaUsedBlankCellSet.BookSheetKey(bookIndex, sheetIndex);
        this._formulaCellCache.applyOperation(new FormulaCellCache.IEntryOperation() {
            @Override
            public void processEntry(final FormulaCellCacheEntry entry) {
                entry.notifyUpdatedBlankCell(bsk, rowIndex, columnIndex, EvaluationCache.this._evaluationListener);
            }
        });
    }
    
    public PlainValueCellCacheEntry getPlainValueEntry(final int bookIndex, final int sheetIndex, final int rowIndex, final int columnIndex, final ValueEval value) {
        final PlainCellCache.Loc loc = new PlainCellCache.Loc(bookIndex, sheetIndex, rowIndex, columnIndex);
        PlainValueCellCacheEntry result = this._plainCellCache.get(loc);
        if (result == null) {
            result = new PlainValueCellCacheEntry(value);
            this._plainCellCache.put(loc, result);
            if (this._evaluationListener != null) {
                this._evaluationListener.onReadPlainValue(sheetIndex, rowIndex, columnIndex, result);
            }
        }
        else {
            if (!this.areValuesEqual(result.getValue(), value)) {
                throw new IllegalStateException("value changed");
            }
            if (this._evaluationListener != null) {
                this._evaluationListener.onCacheHit(sheetIndex, rowIndex, columnIndex, value);
            }
        }
        return result;
    }
    
    private boolean areValuesEqual(final ValueEval a, final ValueEval b) {
        if (a == null) {
            return false;
        }
        final Class<?> cls = a.getClass();
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
    
    public FormulaCellCacheEntry getOrCreateFormulaCellEntry(final EvaluationCell cell) {
        FormulaCellCacheEntry result = this._formulaCellCache.get(cell);
        if (result == null) {
            result = new FormulaCellCacheEntry();
            this._formulaCellCache.put(cell, result);
        }
        return result;
    }
    
    public void clear() {
        if (this._evaluationListener != null) {
            this._evaluationListener.onClearWholeCache();
        }
        this._plainCellCache.clear();
        this._formulaCellCache.clear();
    }
    
    public void notifyDeleteCell(final int bookIndex, final int sheetIndex, final EvaluationCell cell) {
        if (cell.getCellType() == 2) {
            final FormulaCellCacheEntry fcce = this._formulaCellCache.remove(cell);
            if (fcce != null) {
                fcce.setSensitiveInputCells(null);
                fcce.recurseClearCachedFormulaResults(this._evaluationListener);
            }
        }
        else {
            final PlainCellCache.Loc loc = new PlainCellCache.Loc(bookIndex, sheetIndex, cell.getRowIndex(), cell.getColumnIndex());
            final PlainValueCellCacheEntry pcce = this._plainCellCache.get(loc);
            if (pcce != null) {
                pcce.recurseClearCachedFormulaResults(this._evaluationListener);
            }
        }
    }
}
