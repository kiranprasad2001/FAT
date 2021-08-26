// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.record.CFRuleRecord;
import org.apache.poi.ss.usermodel.BorderFormatting;

public final class HSSFBorderFormatting implements BorderFormatting
{
    private final CFRuleRecord cfRuleRecord;
    private final org.apache.poi.hssf.record.cf.BorderFormatting borderFormatting;
    
    protected HSSFBorderFormatting(final CFRuleRecord cfRuleRecord) {
        this.cfRuleRecord = cfRuleRecord;
        this.borderFormatting = cfRuleRecord.getBorderFormatting();
    }
    
    protected org.apache.poi.hssf.record.cf.BorderFormatting getBorderFormattingBlock() {
        return this.borderFormatting;
    }
    
    @Override
    public short getBorderBottom() {
        return (short)this.borderFormatting.getBorderBottom();
    }
    
    @Override
    public short getBorderDiagonal() {
        return (short)this.borderFormatting.getBorderDiagonal();
    }
    
    @Override
    public short getBorderLeft() {
        return (short)this.borderFormatting.getBorderLeft();
    }
    
    @Override
    public short getBorderRight() {
        return (short)this.borderFormatting.getBorderRight();
    }
    
    @Override
    public short getBorderTop() {
        return (short)this.borderFormatting.getBorderTop();
    }
    
    @Override
    public short getBottomBorderColor() {
        return (short)this.borderFormatting.getBottomBorderColor();
    }
    
    @Override
    public short getDiagonalBorderColor() {
        return (short)this.borderFormatting.getDiagonalBorderColor();
    }
    
    @Override
    public short getLeftBorderColor() {
        return (short)this.borderFormatting.getLeftBorderColor();
    }
    
    @Override
    public short getRightBorderColor() {
        return (short)this.borderFormatting.getRightBorderColor();
    }
    
    @Override
    public short getTopBorderColor() {
        return (short)this.borderFormatting.getTopBorderColor();
    }
    
    public boolean isBackwardDiagonalOn() {
        return this.borderFormatting.isBackwardDiagonalOn();
    }
    
    public boolean isForwardDiagonalOn() {
        return this.borderFormatting.isForwardDiagonalOn();
    }
    
    public void setBackwardDiagonalOn(final boolean on) {
        this.borderFormatting.setBackwardDiagonalOn(on);
        if (on) {
            this.cfRuleRecord.setTopLeftBottomRightBorderModified(on);
        }
    }
    
    @Override
    public void setBorderBottom(final short border) {
        this.borderFormatting.setBorderBottom(border);
        if (border != 0) {
            this.cfRuleRecord.setBottomBorderModified(true);
        }
    }
    
    @Override
    public void setBorderDiagonal(final short border) {
        this.borderFormatting.setBorderDiagonal(border);
        if (border != 0) {
            this.cfRuleRecord.setBottomLeftTopRightBorderModified(true);
            this.cfRuleRecord.setTopLeftBottomRightBorderModified(true);
        }
    }
    
    @Override
    public void setBorderLeft(final short border) {
        this.borderFormatting.setBorderLeft(border);
        if (border != 0) {
            this.cfRuleRecord.setLeftBorderModified(true);
        }
    }
    
    @Override
    public void setBorderRight(final short border) {
        this.borderFormatting.setBorderRight(border);
        if (border != 0) {
            this.cfRuleRecord.setRightBorderModified(true);
        }
    }
    
    @Override
    public void setBorderTop(final short border) {
        this.borderFormatting.setBorderTop(border);
        if (border != 0) {
            this.cfRuleRecord.setTopBorderModified(true);
        }
    }
    
    @Override
    public void setBottomBorderColor(final short color) {
        this.borderFormatting.setBottomBorderColor(color);
        if (color != 0) {
            this.cfRuleRecord.setBottomBorderModified(true);
        }
    }
    
    @Override
    public void setDiagonalBorderColor(final short color) {
        this.borderFormatting.setDiagonalBorderColor(color);
        if (color != 0) {
            this.cfRuleRecord.setBottomLeftTopRightBorderModified(true);
            this.cfRuleRecord.setTopLeftBottomRightBorderModified(true);
        }
    }
    
    public void setForwardDiagonalOn(final boolean on) {
        this.borderFormatting.setForwardDiagonalOn(on);
        if (on) {
            this.cfRuleRecord.setBottomLeftTopRightBorderModified(on);
        }
    }
    
    @Override
    public void setLeftBorderColor(final short color) {
        this.borderFormatting.setLeftBorderColor(color);
        if (color != 0) {
            this.cfRuleRecord.setLeftBorderModified(true);
        }
    }
    
    @Override
    public void setRightBorderColor(final short color) {
        this.borderFormatting.setRightBorderColor(color);
        if (color != 0) {
            this.cfRuleRecord.setRightBorderModified(true);
        }
    }
    
    @Override
    public void setTopBorderColor(final short color) {
        this.borderFormatting.setTopBorderColor(color);
        if (color != 0) {
            this.cfRuleRecord.setTopBorderModified(true);
        }
    }
}
