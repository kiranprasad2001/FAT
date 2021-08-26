// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula;

import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;

final class EvaluationTracker
{
    private final List<CellEvaluationFrame> _evaluationFrames;
    private final Set<FormulaCellCacheEntry> _currentlyEvaluatingCells;
    private final EvaluationCache _cache;
    
    public EvaluationTracker(final EvaluationCache cache) {
        this._cache = cache;
        this._evaluationFrames = new ArrayList<CellEvaluationFrame>();
        this._currentlyEvaluatingCells = new HashSet<FormulaCellCacheEntry>();
    }
    
    public boolean startEvaluate(final FormulaCellCacheEntry cce) {
        if (cce == null) {
            throw new IllegalArgumentException("cellLoc must not be null");
        }
        if (this._currentlyEvaluatingCells.contains(cce)) {
            return false;
        }
        this._currentlyEvaluatingCells.add(cce);
        this._evaluationFrames.add(new CellEvaluationFrame(cce));
        return true;
    }
    
    public void updateCacheResult(final ValueEval result) {
        final int nFrames = this._evaluationFrames.size();
        if (nFrames < 1) {
            throw new IllegalStateException("Call to endEvaluate without matching call to startEvaluate");
        }
        final CellEvaluationFrame frame = this._evaluationFrames.get(nFrames - 1);
        if (result == ErrorEval.CIRCULAR_REF_ERROR && nFrames > 1) {
            return;
        }
        frame.updateFormulaResult(result);
    }
    
    public void endEvaluate(final CellCacheEntry cce) {
        int nFrames = this._evaluationFrames.size();
        if (nFrames < 1) {
            throw new IllegalStateException("Call to endEvaluate without matching call to startEvaluate");
        }
        --nFrames;
        final CellEvaluationFrame frame = this._evaluationFrames.get(nFrames);
        if (cce != frame.getCCE()) {
            throw new IllegalStateException("Wrong cell specified. ");
        }
        this._evaluationFrames.remove(nFrames);
        this._currentlyEvaluatingCells.remove(cce);
    }
    
    public void acceptFormulaDependency(final CellCacheEntry cce) {
        final int prevFrameIndex = this._evaluationFrames.size() - 1;
        if (prevFrameIndex >= 0) {
            final CellEvaluationFrame consumingFrame = this._evaluationFrames.get(prevFrameIndex);
            consumingFrame.addSensitiveInputCell(cce);
        }
    }
    
    public void acceptPlainValueDependency(final int bookIndex, final int sheetIndex, final int rowIndex, final int columnIndex, final ValueEval value) {
        final int prevFrameIndex = this._evaluationFrames.size() - 1;
        if (prevFrameIndex >= 0) {
            final CellEvaluationFrame consumingFrame = this._evaluationFrames.get(prevFrameIndex);
            if (value == BlankEval.instance) {
                consumingFrame.addUsedBlankCell(bookIndex, sheetIndex, rowIndex, columnIndex);
            }
            else {
                final PlainValueCellCacheEntry cce = this._cache.getPlainValueEntry(bookIndex, sheetIndex, rowIndex, columnIndex, value);
                consumingFrame.addSensitiveInputCell(cce);
            }
        }
    }
}
