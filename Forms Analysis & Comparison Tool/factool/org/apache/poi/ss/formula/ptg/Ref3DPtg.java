// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.ptg;

import org.apache.poi.ss.formula.FormulaRenderingWorkbook;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.ss.formula.ExternSheetReferenceToken;
import org.apache.poi.ss.formula.WorkbookDependentFormula;

public final class Ref3DPtg extends RefPtgBase implements WorkbookDependentFormula, ExternSheetReferenceToken
{
    public static final byte sid = 58;
    private static final int SIZE = 7;
    private int field_1_index_extern_sheet;
    
    public Ref3DPtg(final LittleEndianInput in) {
        this.field_1_index_extern_sheet = in.readShort();
        this.readCoordinates(in);
    }
    
    public Ref3DPtg(final String cellref, final int externIdx) {
        this(new CellReference(cellref), externIdx);
    }
    
    public Ref3DPtg(final CellReference c, final int externIdx) {
        super(c);
        this.setExternSheetIndex(externIdx);
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(this.getClass().getName());
        sb.append(" [");
        sb.append("sheetIx=").append(this.getExternSheetIndex());
        sb.append(" ! ");
        sb.append(this.formatReferenceAsString());
        sb.append("]");
        return sb.toString();
    }
    
    @Override
    public void write(final LittleEndianOutput out) {
        out.writeByte(58 + this.getPtgClass());
        out.writeShort(this.getExternSheetIndex());
        this.writeCoordinates(out);
    }
    
    @Override
    public int getSize() {
        return 7;
    }
    
    @Override
    public int getExternSheetIndex() {
        return this.field_1_index_extern_sheet;
    }
    
    public void setExternSheetIndex(final int index) {
        this.field_1_index_extern_sheet = index;
    }
    
    @Override
    public String format2DRefAsString() {
        return this.formatReferenceAsString();
    }
    
    @Override
    public String toFormulaString(final FormulaRenderingWorkbook book) {
        return ExternSheetNameResolver.prependSheetName(book, this.field_1_index_extern_sheet, this.formatReferenceAsString());
    }
    
    @Override
    public String toFormulaString() {
        throw new RuntimeException("3D references need a workbook to determine formula text");
    }
}
