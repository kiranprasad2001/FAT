// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula;

import org.apache.poi.ss.formula.ptg.Deleted3DPxg;
import org.apache.poi.ss.formula.ptg.DeletedArea3DPtg;
import org.apache.poi.ss.formula.ptg.AreaErrPtg;
import org.apache.poi.ss.formula.ptg.AreaPtg;
import org.apache.poi.ss.formula.ptg.DeletedRef3DPtg;
import org.apache.poi.ss.formula.ptg.RefErrorPtg;
import org.apache.poi.ss.formula.ptg.Area3DPxg;
import org.apache.poi.ss.formula.ptg.Area3DPtg;
import org.apache.poi.ss.formula.ptg.AreaPtgBase;
import org.apache.poi.ss.formula.ptg.Area2DPtgBase;
import org.apache.poi.ss.formula.ptg.Ref3DPxg;
import org.apache.poi.ss.formula.ptg.Ref3DPtg;
import org.apache.poi.ss.formula.ptg.RefPtgBase;
import org.apache.poi.ss.formula.ptg.RefPtg;
import org.apache.poi.ss.formula.ptg.Ptg;

public final class FormulaShifter
{
    private final int _externSheetIndex;
    private final String _sheetName;
    private final int _firstMovedIndex;
    private final int _lastMovedIndex;
    private final int _amountToMove;
    private final int _srcSheetIndex;
    private final int _dstSheetIndex;
    private final ShiftMode _mode;
    
    private FormulaShifter(final int externSheetIndex, final String sheetName, final int firstMovedIndex, final int lastMovedIndex, final int amountToMove) {
        if (amountToMove == 0) {
            throw new IllegalArgumentException("amountToMove must not be zero");
        }
        if (firstMovedIndex > lastMovedIndex) {
            throw new IllegalArgumentException("firstMovedIndex, lastMovedIndex out of order");
        }
        this._externSheetIndex = externSheetIndex;
        this._sheetName = sheetName;
        this._firstMovedIndex = firstMovedIndex;
        this._lastMovedIndex = lastMovedIndex;
        this._amountToMove = amountToMove;
        this._mode = ShiftMode.Row;
        final int n = -1;
        this._dstSheetIndex = n;
        this._srcSheetIndex = n;
    }
    
    private FormulaShifter(final int srcSheetIndex, final int dstSheetIndex) {
        final int n = -1;
        this._amountToMove = n;
        this._lastMovedIndex = n;
        this._firstMovedIndex = n;
        this._externSheetIndex = n;
        this._sheetName = null;
        this._srcSheetIndex = srcSheetIndex;
        this._dstSheetIndex = dstSheetIndex;
        this._mode = ShiftMode.Sheet;
    }
    
    public static FormulaShifter createForRowShift(final int externSheetIndex, final String sheetName, final int firstMovedRowIndex, final int lastMovedRowIndex, final int numberOfRowsToMove) {
        return new FormulaShifter(externSheetIndex, sheetName, firstMovedRowIndex, lastMovedRowIndex, numberOfRowsToMove);
    }
    
    public static FormulaShifter createForSheetShift(final int srcSheetIndex, final int dstSheetIndex) {
        return new FormulaShifter(srcSheetIndex, dstSheetIndex);
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(this.getClass().getName());
        sb.append(" [");
        sb.append(this._firstMovedIndex);
        sb.append(this._lastMovedIndex);
        sb.append(this._amountToMove);
        return sb.toString();
    }
    
    public boolean adjustFormula(final Ptg[] ptgs, final int currentExternSheetIx) {
        boolean refsWereChanged = false;
        for (int i = 0; i < ptgs.length; ++i) {
            final Ptg newPtg = this.adjustPtg(ptgs[i], currentExternSheetIx);
            if (newPtg != null) {
                refsWereChanged = true;
                ptgs[i] = newPtg;
            }
        }
        return refsWereChanged;
    }
    
    private Ptg adjustPtg(final Ptg ptg, final int currentExternSheetIx) {
        switch (this._mode) {
            case Row: {
                return this.adjustPtgDueToRowMove(ptg, currentExternSheetIx);
            }
            case Sheet: {
                return this.adjustPtgDueToShiftMove(ptg);
            }
            default: {
                throw new IllegalStateException("Unsupported shift mode: " + this._mode);
            }
        }
    }
    
    private Ptg adjustPtgDueToRowMove(final Ptg ptg, final int currentExternSheetIx) {
        if (ptg instanceof RefPtg) {
            if (currentExternSheetIx != this._externSheetIndex) {
                return null;
            }
            final RefPtg rptg = (RefPtg)ptg;
            return this.rowMoveRefPtg(rptg);
        }
        else if (ptg instanceof Ref3DPtg) {
            final Ref3DPtg rptg2 = (Ref3DPtg)ptg;
            if (this._externSheetIndex != rptg2.getExternSheetIndex()) {
                return null;
            }
            return this.rowMoveRefPtg(rptg2);
        }
        else if (ptg instanceof Ref3DPxg) {
            final Ref3DPxg rpxg = (Ref3DPxg)ptg;
            if (rpxg.getExternalWorkbookNumber() > 0 || !this._sheetName.equals(rpxg.getSheetName())) {
                return null;
            }
            return this.rowMoveRefPtg(rpxg);
        }
        else if (ptg instanceof Area2DPtgBase) {
            if (currentExternSheetIx != this._externSheetIndex) {
                return ptg;
            }
            return this.rowMoveAreaPtg((AreaPtgBase)ptg);
        }
        else if (ptg instanceof Area3DPtg) {
            final Area3DPtg aptg = (Area3DPtg)ptg;
            if (this._externSheetIndex != aptg.getExternSheetIndex()) {
                return null;
            }
            return this.rowMoveAreaPtg(aptg);
        }
        else {
            if (!(ptg instanceof Area3DPxg)) {
                return null;
            }
            final Area3DPxg apxg = (Area3DPxg)ptg;
            if (apxg.getExternalWorkbookNumber() > 0 || !this._sheetName.equals(apxg.getSheetName())) {
                return null;
            }
            return this.rowMoveAreaPtg(apxg);
        }
    }
    
    private Ptg adjustPtgDueToShiftMove(final Ptg ptg) {
        Ptg updatedPtg = null;
        if (ptg instanceof Ref3DPtg) {
            final Ref3DPtg ref = (Ref3DPtg)ptg;
            if (ref.getExternSheetIndex() == this._srcSheetIndex) {
                ref.setExternSheetIndex(this._dstSheetIndex);
                updatedPtg = ref;
            }
            else if (ref.getExternSheetIndex() == this._dstSheetIndex) {
                ref.setExternSheetIndex(this._srcSheetIndex);
                updatedPtg = ref;
            }
        }
        return updatedPtg;
    }
    
    private Ptg rowMoveRefPtg(final RefPtgBase rptg) {
        final int refRow = rptg.getRow();
        if (this._firstMovedIndex <= refRow && refRow <= this._lastMovedIndex) {
            rptg.setRow(refRow + this._amountToMove);
            return rptg;
        }
        final int destFirstRowIndex = this._firstMovedIndex + this._amountToMove;
        final int destLastRowIndex = this._lastMovedIndex + this._amountToMove;
        if (destLastRowIndex < refRow || refRow < destFirstRowIndex) {
            return null;
        }
        if (destFirstRowIndex <= refRow && refRow <= destLastRowIndex) {
            return createDeletedRef(rptg);
        }
        throw new IllegalStateException("Situation not covered: (" + this._firstMovedIndex + ", " + this._lastMovedIndex + ", " + this._amountToMove + ", " + refRow + ", " + refRow + ")");
    }
    
    private Ptg rowMoveAreaPtg(final AreaPtgBase aptg) {
        final int aFirstRow = aptg.getFirstRow();
        final int aLastRow = aptg.getLastRow();
        if (this._firstMovedIndex <= aFirstRow && aLastRow <= this._lastMovedIndex) {
            aptg.setFirstRow(aFirstRow + this._amountToMove);
            aptg.setLastRow(aLastRow + this._amountToMove);
            return aptg;
        }
        final int destFirstRowIndex = this._firstMovedIndex + this._amountToMove;
        final int destLastRowIndex = this._lastMovedIndex + this._amountToMove;
        if (aFirstRow < this._firstMovedIndex && this._lastMovedIndex < aLastRow) {
            if (destFirstRowIndex < aFirstRow && aFirstRow <= destLastRowIndex) {
                aptg.setFirstRow(destLastRowIndex + 1);
                return aptg;
            }
            if (destFirstRowIndex <= aLastRow && aLastRow < destLastRowIndex) {
                aptg.setLastRow(destFirstRowIndex - 1);
                return aptg;
            }
            return null;
        }
        else if (this._firstMovedIndex <= aFirstRow && aFirstRow <= this._lastMovedIndex) {
            if (this._amountToMove < 0) {
                aptg.setFirstRow(aFirstRow + this._amountToMove);
                return aptg;
            }
            if (destFirstRowIndex > aLastRow) {
                return null;
            }
            int newFirstRowIx = aFirstRow + this._amountToMove;
            if (destLastRowIndex < aLastRow) {
                aptg.setFirstRow(newFirstRowIx);
                return aptg;
            }
            final int areaRemainingTopRowIx = this._lastMovedIndex + 1;
            if (destFirstRowIndex > areaRemainingTopRowIx) {
                newFirstRowIx = areaRemainingTopRowIx;
            }
            aptg.setFirstRow(newFirstRowIx);
            aptg.setLastRow(Math.max(aLastRow, destLastRowIndex));
            return aptg;
        }
        else if (this._firstMovedIndex <= aLastRow && aLastRow <= this._lastMovedIndex) {
            if (this._amountToMove > 0) {
                aptg.setLastRow(aLastRow + this._amountToMove);
                return aptg;
            }
            if (destLastRowIndex < aFirstRow) {
                return null;
            }
            int newLastRowIx = aLastRow + this._amountToMove;
            if (destFirstRowIndex > aFirstRow) {
                aptg.setLastRow(newLastRowIx);
                return aptg;
            }
            final int areaRemainingBottomRowIx = this._firstMovedIndex - 1;
            if (destLastRowIndex < areaRemainingBottomRowIx) {
                newLastRowIx = areaRemainingBottomRowIx;
            }
            aptg.setFirstRow(Math.min(aFirstRow, destFirstRowIndex));
            aptg.setLastRow(newLastRowIx);
            return aptg;
        }
        else {
            if (destLastRowIndex < aFirstRow || aLastRow < destFirstRowIndex) {
                return null;
            }
            if (destFirstRowIndex <= aFirstRow && aLastRow <= destLastRowIndex) {
                return createDeletedRef(aptg);
            }
            if (aFirstRow <= destFirstRowIndex && destLastRowIndex <= aLastRow) {
                return null;
            }
            if (destFirstRowIndex < aFirstRow && aFirstRow <= destLastRowIndex) {
                aptg.setFirstRow(destLastRowIndex + 1);
                return aptg;
            }
            if (destFirstRowIndex <= aLastRow && aLastRow < destLastRowIndex) {
                aptg.setLastRow(destFirstRowIndex - 1);
                return aptg;
            }
            throw new IllegalStateException("Situation not covered: (" + this._firstMovedIndex + ", " + this._lastMovedIndex + ", " + this._amountToMove + ", " + aFirstRow + ", " + aLastRow + ")");
        }
    }
    
    private static Ptg createDeletedRef(final Ptg ptg) {
        if (ptg instanceof RefPtg) {
            return new RefErrorPtg();
        }
        if (ptg instanceof Ref3DPtg) {
            final Ref3DPtg rptg = (Ref3DPtg)ptg;
            return new DeletedRef3DPtg(rptg.getExternSheetIndex());
        }
        if (ptg instanceof AreaPtg) {
            return new AreaErrPtg();
        }
        if (ptg instanceof Area3DPtg) {
            final Area3DPtg area3DPtg = (Area3DPtg)ptg;
            return new DeletedArea3DPtg(area3DPtg.getExternSheetIndex());
        }
        if (ptg instanceof Ref3DPxg) {
            final Ref3DPxg pxg = (Ref3DPxg)ptg;
            return new Deleted3DPxg(pxg.getExternalWorkbookNumber(), pxg.getSheetName());
        }
        if (ptg instanceof Area3DPxg) {
            final Area3DPxg pxg2 = (Area3DPxg)ptg;
            return new Deleted3DPxg(pxg2.getExternalWorkbookNumber(), pxg2.getSheetName());
        }
        throw new IllegalArgumentException("Unexpected ref ptg class (" + ptg.getClass().getName() + ")");
    }
    
    enum ShiftMode
    {
        Row, 
        Sheet;
    }
}
