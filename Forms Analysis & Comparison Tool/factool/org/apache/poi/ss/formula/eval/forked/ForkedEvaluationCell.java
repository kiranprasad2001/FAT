// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.eval.forked;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.EvaluationSheet;
import org.apache.poi.ss.formula.EvaluationCell;

final class ForkedEvaluationCell implements EvaluationCell
{
    private final EvaluationSheet _sheet;
    private final EvaluationCell _masterCell;
    private boolean _booleanValue;
    private int _cellType;
    private int _errorValue;
    private double _numberValue;
    private String _stringValue;
    
    public ForkedEvaluationCell(final ForkedEvaluationSheet sheet, final EvaluationCell masterCell) {
        this._sheet = sheet;
        this._masterCell = masterCell;
        this.setValue(BlankEval.instance);
    }
    
    @Override
    public Object getIdentityKey() {
        return this._masterCell.getIdentityKey();
    }
    
    public void setValue(final ValueEval value) {
        final Class<? extends ValueEval> cls = value.getClass();
        if (cls == NumberEval.class) {
            this._cellType = 0;
            this._numberValue = ((NumberEval)value).getNumberValue();
            return;
        }
        if (cls == StringEval.class) {
            this._cellType = 1;
            this._stringValue = ((StringEval)value).getStringValue();
            return;
        }
        if (cls == BoolEval.class) {
            this._cellType = 4;
            this._booleanValue = ((BoolEval)value).getBooleanValue();
            return;
        }
        if (cls == ErrorEval.class) {
            this._cellType = 5;
            this._errorValue = ((ErrorEval)value).getErrorCode();
            return;
        }
        if (cls == BlankEval.class) {
            this._cellType = 3;
            return;
        }
        throw new IllegalArgumentException("Unexpected value class (" + cls.getName() + ")");
    }
    
    public void copyValue(final Cell destCell) {
        switch (this._cellType) {
            case 3: {
                destCell.setCellType(3);
            }
            case 0: {
                destCell.setCellValue(this._numberValue);
            }
            case 4: {
                destCell.setCellValue(this._booleanValue);
            }
            case 1: {
                destCell.setCellValue(this._stringValue);
            }
            case 5: {
                destCell.setCellErrorValue((byte)this._errorValue);
            }
            default: {
                throw new IllegalStateException("Unexpected data type (" + this._cellType + ")");
            }
        }
    }
    
    private void checkCellType(final int expectedCellType) {
        if (this._cellType != expectedCellType) {
            throw new RuntimeException("Wrong data type (" + this._cellType + ")");
        }
    }
    
    @Override
    public int getCellType() {
        return this._cellType;
    }
    
    @Override
    public boolean getBooleanCellValue() {
        this.checkCellType(4);
        return this._booleanValue;
    }
    
    @Override
    public int getErrorCellValue() {
        this.checkCellType(5);
        return this._errorValue;
    }
    
    @Override
    public double getNumericCellValue() {
        this.checkCellType(0);
        return this._numberValue;
    }
    
    @Override
    public String getStringCellValue() {
        this.checkCellType(1);
        return this._stringValue;
    }
    
    @Override
    public EvaluationSheet getSheet() {
        return this._sheet;
    }
    
    @Override
    public int getRowIndex() {
        return this._masterCell.getRowIndex();
    }
    
    @Override
    public int getColumnIndex() {
        return this._masterCell.getColumnIndex();
    }
    
    @Override
    public int getCachedFormulaResultType() {
        return this._masterCell.getCachedFormulaResultType();
    }
}
