// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

import org.apache.poi.ss.formula.eval.ErrorEval;

public final class CellValue
{
    public static final CellValue TRUE;
    public static final CellValue FALSE;
    private final int _cellType;
    private final double _numberValue;
    private final boolean _booleanValue;
    private final String _textValue;
    private final int _errorCode;
    
    private CellValue(final int cellType, final double numberValue, final boolean booleanValue, final String textValue, final int errorCode) {
        this._cellType = cellType;
        this._numberValue = numberValue;
        this._booleanValue = booleanValue;
        this._textValue = textValue;
        this._errorCode = errorCode;
    }
    
    public CellValue(final double numberValue) {
        this(0, numberValue, false, null, 0);
    }
    
    public static CellValue valueOf(final boolean booleanValue) {
        return booleanValue ? CellValue.TRUE : CellValue.FALSE;
    }
    
    public CellValue(final String stringValue) {
        this(1, 0.0, false, stringValue, 0);
    }
    
    public static CellValue getError(final int errorCode) {
        return new CellValue(5, 0.0, false, null, errorCode);
    }
    
    public boolean getBooleanValue() {
        return this._booleanValue;
    }
    
    public double getNumberValue() {
        return this._numberValue;
    }
    
    public String getStringValue() {
        return this._textValue;
    }
    
    public int getCellType() {
        return this._cellType;
    }
    
    public byte getErrorValue() {
        return (byte)this._errorCode;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(64);
        sb.append(this.getClass().getName()).append(" [");
        sb.append(this.formatAsString());
        sb.append("]");
        return sb.toString();
    }
    
    public String formatAsString() {
        switch (this._cellType) {
            case 0: {
                return String.valueOf(this._numberValue);
            }
            case 1: {
                return '\"' + this._textValue + '\"';
            }
            case 4: {
                return this._booleanValue ? "TRUE" : "FALSE";
            }
            case 5: {
                return ErrorEval.getText(this._errorCode);
            }
            default: {
                return "<error unexpected cell type " + this._cellType + ">";
            }
        }
    }
    
    static {
        TRUE = new CellValue(4, 0.0, true, null, 0);
        FALSE = new CellValue(4, 0.0, false, null, 0);
    }
}
