// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.record.FontRecord;
import org.apache.poi.ss.usermodel.Font;

public final class HSSFFont implements Font
{
    public static final String FONT_ARIAL = "Arial";
    private FontRecord font;
    private short index;
    
    protected HSSFFont(final short index, final FontRecord rec) {
        this.font = rec;
        this.index = index;
    }
    
    @Override
    public void setFontName(final String name) {
        this.font.setFontName(name);
    }
    
    @Override
    public String getFontName() {
        return this.font.getFontName();
    }
    
    @Override
    public short getIndex() {
        return this.index;
    }
    
    @Override
    public void setFontHeight(final short height) {
        this.font.setFontHeight(height);
    }
    
    @Override
    public void setFontHeightInPoints(final short height) {
        this.font.setFontHeight((short)(height * 20));
    }
    
    @Override
    public short getFontHeight() {
        return this.font.getFontHeight();
    }
    
    @Override
    public short getFontHeightInPoints() {
        return (short)(this.font.getFontHeight() / 20);
    }
    
    @Override
    public void setItalic(final boolean italic) {
        this.font.setItalic(italic);
    }
    
    @Override
    public boolean getItalic() {
        return this.font.isItalic();
    }
    
    @Override
    public void setStrikeout(final boolean strikeout) {
        this.font.setStrikeout(strikeout);
    }
    
    @Override
    public boolean getStrikeout() {
        return this.font.isStruckout();
    }
    
    @Override
    public void setColor(final short color) {
        this.font.setColorPaletteIndex(color);
    }
    
    @Override
    public short getColor() {
        return this.font.getColorPaletteIndex();
    }
    
    public HSSFColor getHSSFColor(final HSSFWorkbook wb) {
        final HSSFPalette pallette = wb.getCustomPalette();
        return pallette.getColor(this.getColor());
    }
    
    @Override
    public void setBoldweight(final short boldweight) {
        this.font.setBoldWeight(boldweight);
    }
    
    @Override
    public void setBold(final boolean bold) {
        if (bold) {
            this.font.setBoldWeight((short)700);
        }
        else {
            this.font.setBoldWeight((short)400);
        }
    }
    
    @Override
    public short getBoldweight() {
        return this.font.getBoldWeight();
    }
    
    @Override
    public boolean getBold() {
        return this.getBoldweight() == 700;
    }
    
    @Override
    public void setTypeOffset(final short offset) {
        this.font.setSuperSubScript(offset);
    }
    
    @Override
    public short getTypeOffset() {
        return this.font.getSuperSubScript();
    }
    
    @Override
    public void setUnderline(final byte underline) {
        this.font.setUnderline(underline);
    }
    
    @Override
    public byte getUnderline() {
        return this.font.getUnderline();
    }
    
    @Override
    public int getCharSet() {
        final byte charset = this.font.getCharset();
        if (charset >= 0) {
            return charset;
        }
        return charset + 256;
    }
    
    @Override
    public void setCharSet(final int charset) {
        byte cs = (byte)charset;
        if (charset > 127) {
            cs = (byte)(charset - 256);
        }
        this.setCharSet(cs);
    }
    
    @Override
    public void setCharSet(final byte charset) {
        this.font.setCharset(charset);
    }
    
    @Override
    public String toString() {
        return "org.apache.poi.hssf.usermodel.HSSFFont{" + this.font + "}";
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this.font == null) ? 0 : this.font.hashCode());
        result = 31 * result + this.index;
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof HSSFFont) {
            final HSSFFont other = (HSSFFont)obj;
            if (this.font == null) {
                if (other.font != null) {
                    return false;
                }
            }
            else if (!this.font.equals(other.font)) {
                return false;
            }
            return this.index == other.index;
        }
        return false;
    }
}
