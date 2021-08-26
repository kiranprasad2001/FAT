// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.record.CFRuleRecord;
import org.apache.poi.ss.usermodel.FontFormatting;

public final class HSSFFontFormatting implements FontFormatting
{
    public static final byte U_NONE = 0;
    public static final byte U_SINGLE = 1;
    public static final byte U_DOUBLE = 2;
    public static final byte U_SINGLE_ACCOUNTING = 33;
    public static final byte U_DOUBLE_ACCOUNTING = 34;
    private final org.apache.poi.hssf.record.cf.FontFormatting fontFormatting;
    
    protected HSSFFontFormatting(final CFRuleRecord cfRuleRecord) {
        this.fontFormatting = cfRuleRecord.getFontFormatting();
    }
    
    protected org.apache.poi.hssf.record.cf.FontFormatting getFontFormattingBlock() {
        return this.fontFormatting;
    }
    
    @Override
    public short getEscapementType() {
        return this.fontFormatting.getEscapementType();
    }
    
    @Override
    public short getFontColorIndex() {
        return this.fontFormatting.getFontColorIndex();
    }
    
    @Override
    public int getFontHeight() {
        return this.fontFormatting.getFontHeight();
    }
    
    public short getFontWeight() {
        return this.fontFormatting.getFontWeight();
    }
    
    protected byte[] getRawRecord() {
        return this.fontFormatting.getRawRecord();
    }
    
    @Override
    public short getUnderlineType() {
        return this.fontFormatting.getUnderlineType();
    }
    
    @Override
    public boolean isBold() {
        return this.fontFormatting.isFontWeightModified() && this.fontFormatting.isBold();
    }
    
    public boolean isEscapementTypeModified() {
        return this.fontFormatting.isEscapementTypeModified();
    }
    
    public boolean isFontCancellationModified() {
        return this.fontFormatting.isFontCancellationModified();
    }
    
    public boolean isFontOutlineModified() {
        return this.fontFormatting.isFontOutlineModified();
    }
    
    public boolean isFontShadowModified() {
        return this.fontFormatting.isFontShadowModified();
    }
    
    public boolean isFontStyleModified() {
        return this.fontFormatting.isFontStyleModified();
    }
    
    @Override
    public boolean isItalic() {
        return this.fontFormatting.isFontStyleModified() && this.fontFormatting.isItalic();
    }
    
    public boolean isOutlineOn() {
        return this.fontFormatting.isFontOutlineModified() && this.fontFormatting.isOutlineOn();
    }
    
    public boolean isShadowOn() {
        return this.fontFormatting.isFontOutlineModified() && this.fontFormatting.isShadowOn();
    }
    
    public boolean isStruckout() {
        return this.fontFormatting.isFontCancellationModified() && this.fontFormatting.isStruckout();
    }
    
    public boolean isUnderlineTypeModified() {
        return this.fontFormatting.isUnderlineTypeModified();
    }
    
    public boolean isFontWeightModified() {
        return this.fontFormatting.isFontWeightModified();
    }
    
    @Override
    public void setFontStyle(final boolean italic, final boolean bold) {
        final boolean modified = italic || bold;
        this.fontFormatting.setItalic(italic);
        this.fontFormatting.setBold(bold);
        this.fontFormatting.setFontStyleModified(modified);
        this.fontFormatting.setFontWieghtModified(modified);
    }
    
    @Override
    public void resetFontStyle() {
        this.setFontStyle(false, false);
    }
    
    @Override
    public void setEscapementType(final short escapementType) {
        switch (escapementType) {
            case 1:
            case 2: {
                this.fontFormatting.setEscapementType(escapementType);
                this.fontFormatting.setEscapementTypeModified(true);
                break;
            }
            case 0: {
                this.fontFormatting.setEscapementType(escapementType);
                this.fontFormatting.setEscapementTypeModified(false);
                break;
            }
        }
    }
    
    public void setEscapementTypeModified(final boolean modified) {
        this.fontFormatting.setEscapementTypeModified(modified);
    }
    
    public void setFontCancellationModified(final boolean modified) {
        this.fontFormatting.setFontCancellationModified(modified);
    }
    
    @Override
    public void setFontColorIndex(final short fci) {
        this.fontFormatting.setFontColorIndex(fci);
    }
    
    @Override
    public void setFontHeight(final int height) {
        this.fontFormatting.setFontHeight(height);
    }
    
    public void setFontOutlineModified(final boolean modified) {
        this.fontFormatting.setFontOutlineModified(modified);
    }
    
    public void setFontShadowModified(final boolean modified) {
        this.fontFormatting.setFontShadowModified(modified);
    }
    
    public void setFontStyleModified(final boolean modified) {
        this.fontFormatting.setFontStyleModified(modified);
    }
    
    public void setOutline(final boolean on) {
        this.fontFormatting.setOutline(on);
        this.fontFormatting.setFontOutlineModified(on);
    }
    
    public void setShadow(final boolean on) {
        this.fontFormatting.setShadow(on);
        this.fontFormatting.setFontShadowModified(on);
    }
    
    public void setStrikeout(final boolean strike) {
        this.fontFormatting.setStrikeout(strike);
        this.fontFormatting.setFontCancellationModified(strike);
    }
    
    @Override
    public void setUnderlineType(final short underlineType) {
        switch (underlineType) {
            case 1:
            case 2:
            case 33:
            case 34: {
                this.fontFormatting.setUnderlineType(underlineType);
                this.setUnderlineTypeModified(true);
                break;
            }
            case 0: {
                this.fontFormatting.setUnderlineType(underlineType);
                this.setUnderlineTypeModified(false);
                break;
            }
        }
    }
    
    public void setUnderlineTypeModified(final boolean modified) {
        this.fontFormatting.setUnderlineTypeModified(modified);
    }
}
