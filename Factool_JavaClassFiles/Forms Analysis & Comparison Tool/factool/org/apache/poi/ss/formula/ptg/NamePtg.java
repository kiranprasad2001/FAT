// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.ptg;

import org.apache.poi.ss.formula.FormulaRenderingWorkbook;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.ss.formula.WorkbookDependentFormula;

public final class NamePtg extends OperandPtg implements WorkbookDependentFormula
{
    public static final short sid = 35;
    private static final int SIZE = 5;
    private int field_1_label_index;
    private short field_2_zero;
    
    public NamePtg(final int nameIndex) {
        this.field_1_label_index = 1 + nameIndex;
    }
    
    public NamePtg(final LittleEndianInput in) {
        this.field_1_label_index = in.readShort();
        this.field_2_zero = in.readShort();
    }
    
    public int getIndex() {
        return this.field_1_label_index - 1;
    }
    
    @Override
    public void write(final LittleEndianOutput out) {
        out.writeByte(35 + this.getPtgClass());
        out.writeShort(this.field_1_label_index);
        out.writeShort(this.field_2_zero);
    }
    
    @Override
    public int getSize() {
        return 5;
    }
    
    @Override
    public String toFormulaString(final FormulaRenderingWorkbook book) {
        return book.getNameText(this);
    }
    
    @Override
    public String toFormulaString() {
        throw new RuntimeException("3D references need a workbook to determine formula text");
    }
    
    @Override
    public byte getDefaultOperandClass() {
        return 0;
    }
}
