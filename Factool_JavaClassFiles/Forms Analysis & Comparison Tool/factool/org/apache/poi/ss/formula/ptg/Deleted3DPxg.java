// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.ptg;

import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.ss.formula.SheetNameFormatter;
import org.apache.poi.ss.usermodel.ErrorConstants;

public final class Deleted3DPxg extends OperandPtg implements Pxg
{
    private int externalWorkbookNumber;
    private String sheetName;
    
    public Deleted3DPxg(final int externalWorkbookNumber, final String sheetName) {
        this.externalWorkbookNumber = -1;
        this.externalWorkbookNumber = externalWorkbookNumber;
        this.sheetName = sheetName;
    }
    
    public Deleted3DPxg(final String sheetName) {
        this(-1, sheetName);
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(this.getClass().getName());
        sb.append(" [");
        if (this.externalWorkbookNumber >= 0) {
            sb.append(" [");
            sb.append("workbook=").append(this.getExternalWorkbookNumber());
            sb.append("] ");
        }
        sb.append("sheet=").append(this.getSheetName());
        sb.append(" ! ");
        sb.append(ErrorConstants.getText(23));
        sb.append("]");
        return sb.toString();
    }
    
    @Override
    public int getExternalWorkbookNumber() {
        return this.externalWorkbookNumber;
    }
    
    @Override
    public String getSheetName() {
        return this.sheetName;
    }
    
    @Override
    public void setSheetName(final String sheetName) {
        this.sheetName = sheetName;
    }
    
    @Override
    public String toFormulaString() {
        final StringBuffer sb = new StringBuffer();
        if (this.externalWorkbookNumber >= 0) {
            sb.append('[');
            sb.append(this.externalWorkbookNumber);
            sb.append(']');
        }
        if (this.sheetName != null) {
            SheetNameFormatter.appendFormat(sb, this.sheetName);
        }
        sb.append('!');
        sb.append(ErrorConstants.getText(23));
        return sb.toString();
    }
    
    @Override
    public byte getDefaultOperandClass() {
        return 32;
    }
    
    @Override
    public int getSize() {
        return 1;
    }
    
    @Override
    public void write(final LittleEndianOutput out) {
        throw new IllegalStateException("XSSF-only Ptg, should not be serialised");
    }
}
