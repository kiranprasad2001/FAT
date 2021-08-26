// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.ptg;

import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.ss.formula.SheetNameFormatter;
import org.apache.poi.ss.formula.SheetRangeIdentifier;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.formula.SheetIdentifier;

public final class Area3DPxg extends AreaPtgBase implements Pxg3D
{
    private int externalWorkbookNumber;
    private String firstSheetName;
    private String lastSheetName;
    
    public Area3DPxg(final int externalWorkbookNumber, final SheetIdentifier sheetName, final String arearef) {
        this(externalWorkbookNumber, sheetName, new AreaReference(arearef));
    }
    
    public Area3DPxg(final int externalWorkbookNumber, final SheetIdentifier sheetName, final AreaReference arearef) {
        super(arearef);
        this.externalWorkbookNumber = -1;
        this.externalWorkbookNumber = externalWorkbookNumber;
        this.firstSheetName = sheetName.getSheetIdentifier().getName();
        if (sheetName instanceof SheetRangeIdentifier) {
            this.lastSheetName = ((SheetRangeIdentifier)sheetName).getLastSheetIdentifier().getName();
        }
        else {
            this.lastSheetName = null;
        }
    }
    
    public Area3DPxg(final SheetIdentifier sheetName, final String arearef) {
        this(sheetName, new AreaReference(arearef));
    }
    
    public Area3DPxg(final SheetIdentifier sheetName, final AreaReference arearef) {
        this(-1, sheetName, arearef);
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
        if (this.lastSheetName != null) {
            sb.append(" : ");
            sb.append("sheet=").append(this.lastSheetName);
        }
        sb.append(" ! ");
        sb.append(this.formatReferenceAsString());
        sb.append("]");
        return sb.toString();
    }
    
    @Override
    public int getExternalWorkbookNumber() {
        return this.externalWorkbookNumber;
    }
    
    @Override
    public String getSheetName() {
        return this.firstSheetName;
    }
    
    @Override
    public String getLastSheetName() {
        return this.lastSheetName;
    }
    
    @Override
    public void setSheetName(final String sheetName) {
        this.firstSheetName = sheetName;
    }
    
    @Override
    public void setLastSheetName(final String sheetName) {
        this.lastSheetName = sheetName;
    }
    
    public String format2DRefAsString() {
        return this.formatReferenceAsString();
    }
    
    @Override
    public String toFormulaString() {
        final StringBuffer sb = new StringBuffer();
        if (this.externalWorkbookNumber >= 0) {
            sb.append('[');
            sb.append(this.externalWorkbookNumber);
            sb.append(']');
        }
        SheetNameFormatter.appendFormat(sb, this.firstSheetName);
        if (this.lastSheetName != null) {
            sb.append(':');
            SheetNameFormatter.appendFormat(sb, this.lastSheetName);
        }
        sb.append('!');
        sb.append(this.formatReferenceAsString());
        return sb.toString();
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
