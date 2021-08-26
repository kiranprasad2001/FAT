// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.ptg;

import org.apache.poi.ss.formula.FormulaRenderingWorkbook;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.ss.formula.WorkbookDependentFormula;

public final class NameXPtg extends OperandPtg implements WorkbookDependentFormula
{
    public static final short sid = 57;
    private static final int SIZE = 7;
    private final int _sheetRefIndex;
    private final int _nameNumber;
    private final int _reserved;
    
    private NameXPtg(final int sheetRefIndex, final int nameNumber, final int reserved) {
        this._sheetRefIndex = sheetRefIndex;
        this._nameNumber = nameNumber;
        this._reserved = reserved;
    }
    
    public NameXPtg(final int sheetRefIndex, final int nameIndex) {
        this(sheetRefIndex, nameIndex + 1, 0);
    }
    
    public NameXPtg(final LittleEndianInput in) {
        this(in.readUShort(), in.readUShort(), in.readUShort());
    }
    
    @Override
    public void write(final LittleEndianOutput out) {
        out.writeByte(57 + this.getPtgClass());
        out.writeShort(this._sheetRefIndex);
        out.writeShort(this._nameNumber);
        out.writeShort(this._reserved);
    }
    
    @Override
    public int getSize() {
        return 7;
    }
    
    @Override
    public String toFormulaString(final FormulaRenderingWorkbook book) {
        return book.resolveNameXText(this);
    }
    
    @Override
    public String toFormulaString() {
        throw new RuntimeException("3D references need a workbook to determine formula text");
    }
    
    @Override
    public String toString() {
        final String retValue = "NameXPtg:[sheetRefIndex:" + this._sheetRefIndex + " , nameNumber:" + this._nameNumber + "]";
        return retValue;
    }
    
    @Override
    public byte getDefaultOperandClass() {
        return 32;
    }
    
    public int getSheetRefIndex() {
        return this._sheetRefIndex;
    }
    
    public int getNameIndex() {
        return this._nameNumber - 1;
    }
}
