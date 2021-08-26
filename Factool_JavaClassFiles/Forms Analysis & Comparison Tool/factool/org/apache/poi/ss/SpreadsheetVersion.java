// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss;

import org.apache.poi.ss.util.CellReference;

public enum SpreadsheetVersion
{
    EXCEL97(65536, 256, 30, 3, 4000, 32767), 
    EXCEL2007(1048576, 16384, 255, Integer.MAX_VALUE, 64000, 32767);
    
    private final int _maxRows;
    private final int _maxColumns;
    private final int _maxFunctionArgs;
    private final int _maxCondFormats;
    private final int _maxCellStyles;
    private final int _maxTextLength;
    
    private SpreadsheetVersion(final int maxRows, final int maxColumns, final int maxFunctionArgs, final int maxCondFormats, final int maxCellStyles, final int maxText) {
        this._maxRows = maxRows;
        this._maxColumns = maxColumns;
        this._maxFunctionArgs = maxFunctionArgs;
        this._maxCondFormats = maxCondFormats;
        this._maxCellStyles = maxCellStyles;
        this._maxTextLength = maxText;
    }
    
    public int getMaxRows() {
        return this._maxRows;
    }
    
    public int getLastRowIndex() {
        return this._maxRows - 1;
    }
    
    public int getMaxColumns() {
        return this._maxColumns;
    }
    
    public int getLastColumnIndex() {
        return this._maxColumns - 1;
    }
    
    public int getMaxFunctionArgs() {
        return this._maxFunctionArgs;
    }
    
    public int getMaxConditionalFormats() {
        return this._maxCondFormats;
    }
    
    public int getMaxCellStyles() {
        return this._maxCellStyles;
    }
    
    public String getLastColumnName() {
        return CellReference.convertNumToColString(this.getLastColumnIndex());
    }
    
    public int getMaxTextLength() {
        return this._maxTextLength;
    }
}
