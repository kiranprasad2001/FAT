// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.ptg;

import org.apache.poi.ss.formula.FormulaRenderingWorkbook;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.formula.ExternSheetReferenceToken;
import org.apache.poi.ss.formula.WorkbookDependentFormula;

public final class Area3DPtg extends AreaPtgBase implements WorkbookDependentFormula, ExternSheetReferenceToken
{
    public static final byte sid = 59;
    private static final int SIZE = 11;
    private int field_1_index_extern_sheet;
    
    public Area3DPtg(final String arearef, final int externIdx) {
        super(new AreaReference(arearef));
        this.setExternSheetIndex(externIdx);
    }
    
    public Area3DPtg(final LittleEndianInput in) {
        this.field_1_index_extern_sheet = in.readShort();
        this.readCoordinates(in);
    }
    
    public Area3DPtg(final int firstRow, final int lastRow, final int firstColumn, final int lastColumn, final boolean firstRowRelative, final boolean lastRowRelative, final boolean firstColRelative, final boolean lastColRelative, final int externalSheetIndex) {
        super(firstRow, lastRow, firstColumn, lastColumn, firstRowRelative, lastRowRelative, firstColRelative, lastColRelative);
        this.setExternSheetIndex(externalSheetIndex);
    }
    
    public Area3DPtg(final AreaReference arearef, final int externIdx) {
        super(arearef);
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
        out.writeByte(59 + this.getPtgClass());
        out.writeShort(this.field_1_index_extern_sheet);
        this.writeCoordinates(out);
    }
    
    @Override
    public int getSize() {
        return 11;
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
