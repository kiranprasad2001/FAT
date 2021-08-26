// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.hssf.record.FontRecord;
import org.apache.poi.hssf.record.StyleRecord;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.hssf.record.FormatRecord;
import java.util.List;
import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.hssf.record.ExtendedFormatRecord;
import org.apache.poi.ss.usermodel.CellStyle;

public final class HSSFCellStyle implements CellStyle
{
    private ExtendedFormatRecord _format;
    private short _index;
    private InternalWorkbook _workbook;
    private static ThreadLocal<Short> lastDateFormat;
    private static ThreadLocal<List<FormatRecord>> lastFormats;
    private static ThreadLocal<String> getDataFormatStringCache;
    
    protected HSSFCellStyle(final short index, final ExtendedFormatRecord rec, final HSSFWorkbook workbook) {
        this(index, rec, workbook.getWorkbook());
    }
    
    protected HSSFCellStyle(final short index, final ExtendedFormatRecord rec, final InternalWorkbook workbook) {
        this._format = null;
        this._index = 0;
        this._workbook = null;
        this._workbook = workbook;
        this._index = index;
        this._format = rec;
    }
    
    @Override
    public short getIndex() {
        return this._index;
    }
    
    public HSSFCellStyle getParentStyle() {
        final short parentIndex = this._format.getParentIndex();
        if (parentIndex == 0 || parentIndex == 4095) {
            return null;
        }
        return new HSSFCellStyle(parentIndex, this._workbook.getExFormatAt(parentIndex), this._workbook);
    }
    
    @Override
    public void setDataFormat(final short fmt) {
        this._format.setFormatIndex(fmt);
    }
    
    @Override
    public short getDataFormat() {
        return this._format.getFormatIndex();
    }
    
    @Override
    public String getDataFormatString() {
        if (HSSFCellStyle.getDataFormatStringCache.get() != null && HSSFCellStyle.lastDateFormat.get() == this.getDataFormat() && this._workbook.getFormats().equals(HSSFCellStyle.lastFormats.get())) {
            return HSSFCellStyle.getDataFormatStringCache.get();
        }
        HSSFCellStyle.lastFormats.set(this._workbook.getFormats());
        HSSFCellStyle.lastDateFormat.set(this.getDataFormat());
        HSSFCellStyle.getDataFormatStringCache.set(this.getDataFormatString(this._workbook));
        return HSSFCellStyle.getDataFormatStringCache.get();
    }
    
    public String getDataFormatString(final Workbook workbook) {
        final HSSFDataFormat format = new HSSFDataFormat(((HSSFWorkbook)workbook).getWorkbook());
        final int idx = this.getDataFormat();
        return (idx == -1) ? "General" : format.getFormat(this.getDataFormat());
    }
    
    public String getDataFormatString(final InternalWorkbook workbook) {
        final HSSFDataFormat format = new HSSFDataFormat(workbook);
        return format.getFormat(this.getDataFormat());
    }
    
    @Override
    public void setFont(final Font font) {
        this.setFont((HSSFFont)font);
    }
    
    public void setFont(final HSSFFont font) {
        this._format.setIndentNotParentFont(true);
        final short fontindex = font.getIndex();
        this._format.setFontIndex(fontindex);
    }
    
    @Override
    public short getFontIndex() {
        return this._format.getFontIndex();
    }
    
    public HSSFFont getFont(final Workbook parentWorkbook) {
        return ((HSSFWorkbook)parentWorkbook).getFontAt(this.getFontIndex());
    }
    
    @Override
    public void setHidden(final boolean hidden) {
        this._format.setIndentNotParentCellOptions(true);
        this._format.setHidden(hidden);
    }
    
    @Override
    public boolean getHidden() {
        return this._format.isHidden();
    }
    
    @Override
    public void setLocked(final boolean locked) {
        this._format.setIndentNotParentCellOptions(true);
        this._format.setLocked(locked);
    }
    
    @Override
    public boolean getLocked() {
        return this._format.isLocked();
    }
    
    @Override
    public void setAlignment(final short align) {
        this._format.setIndentNotParentAlignment(true);
        this._format.setAlignment(align);
    }
    
    @Override
    public short getAlignment() {
        return this._format.getAlignment();
    }
    
    @Override
    public void setWrapText(final boolean wrapped) {
        this._format.setIndentNotParentAlignment(true);
        this._format.setWrapText(wrapped);
    }
    
    @Override
    public boolean getWrapText() {
        return this._format.getWrapText();
    }
    
    @Override
    public void setVerticalAlignment(final short align) {
        this._format.setVerticalAlignment(align);
    }
    
    @Override
    public short getVerticalAlignment() {
        return this._format.getVerticalAlignment();
    }
    
    @Override
    public void setRotation(short rotation) {
        if (rotation != 255) {
            if (rotation < 0 && rotation >= -90) {
                rotation = (short)(90 - rotation);
            }
            else if (rotation < -90 || rotation > 90) {
                throw new IllegalArgumentException("The rotation must be between -90 and 90 degrees, or 0xff");
            }
        }
        this._format.setRotation(rotation);
    }
    
    @Override
    public short getRotation() {
        short rotation = this._format.getRotation();
        if (rotation == 255) {
            return rotation;
        }
        if (rotation > 90) {
            rotation = (short)(90 - rotation);
        }
        return rotation;
    }
    
    @Override
    public void setIndention(final short indent) {
        this._format.setIndent(indent);
    }
    
    @Override
    public short getIndention() {
        return this._format.getIndent();
    }
    
    @Override
    public void setBorderLeft(final short border) {
        this._format.setIndentNotParentBorder(true);
        this._format.setBorderLeft(border);
    }
    
    @Override
    public short getBorderLeft() {
        return this._format.getBorderLeft();
    }
    
    @Override
    public void setBorderRight(final short border) {
        this._format.setIndentNotParentBorder(true);
        this._format.setBorderRight(border);
    }
    
    @Override
    public short getBorderRight() {
        return this._format.getBorderRight();
    }
    
    @Override
    public void setBorderTop(final short border) {
        this._format.setIndentNotParentBorder(true);
        this._format.setBorderTop(border);
    }
    
    @Override
    public short getBorderTop() {
        return this._format.getBorderTop();
    }
    
    @Override
    public void setBorderBottom(final short border) {
        this._format.setIndentNotParentBorder(true);
        this._format.setBorderBottom(border);
    }
    
    @Override
    public short getBorderBottom() {
        return this._format.getBorderBottom();
    }
    
    @Override
    public void setLeftBorderColor(final short color) {
        this._format.setLeftBorderPaletteIdx(color);
    }
    
    @Override
    public short getLeftBorderColor() {
        return this._format.getLeftBorderPaletteIdx();
    }
    
    @Override
    public void setRightBorderColor(final short color) {
        this._format.setRightBorderPaletteIdx(color);
    }
    
    @Override
    public short getRightBorderColor() {
        return this._format.getRightBorderPaletteIdx();
    }
    
    @Override
    public void setTopBorderColor(final short color) {
        this._format.setTopBorderPaletteIdx(color);
    }
    
    @Override
    public short getTopBorderColor() {
        return this._format.getTopBorderPaletteIdx();
    }
    
    @Override
    public void setBottomBorderColor(final short color) {
        this._format.setBottomBorderPaletteIdx(color);
    }
    
    @Override
    public short getBottomBorderColor() {
        return this._format.getBottomBorderPaletteIdx();
    }
    
    @Override
    public void setFillPattern(final short fp) {
        this._format.setAdtlFillPattern(fp);
    }
    
    @Override
    public short getFillPattern() {
        return this._format.getAdtlFillPattern();
    }
    
    private void checkDefaultBackgroundFills() {
        if (this._format.getFillForeground() == 64) {
            if (this._format.getFillBackground() != 65) {
                this.setFillBackgroundColor((short)65);
            }
        }
        else if (this._format.getFillBackground() == 65 && this._format.getFillForeground() != 64) {
            this.setFillBackgroundColor((short)64);
        }
    }
    
    @Override
    public void setFillBackgroundColor(final short bg) {
        this._format.setFillBackground(bg);
        this.checkDefaultBackgroundFills();
    }
    
    @Override
    public short getFillBackgroundColor() {
        final short result = this._format.getFillBackground();
        if (result == 65) {
            return 64;
        }
        return result;
    }
    
    @Override
    public HSSFColor getFillBackgroundColorColor() {
        final HSSFPalette pallette = new HSSFPalette(this._workbook.getCustomPalette());
        return pallette.getColor(this.getFillBackgroundColor());
    }
    
    @Override
    public void setFillForegroundColor(final short bg) {
        this._format.setFillForeground(bg);
        this.checkDefaultBackgroundFills();
    }
    
    @Override
    public short getFillForegroundColor() {
        return this._format.getFillForeground();
    }
    
    @Override
    public HSSFColor getFillForegroundColorColor() {
        final HSSFPalette pallette = new HSSFPalette(this._workbook.getCustomPalette());
        return pallette.getColor(this.getFillForegroundColor());
    }
    
    public String getUserStyleName() {
        final StyleRecord sr = this._workbook.getStyleRecord(this._index);
        if (sr == null) {
            return null;
        }
        if (sr.isBuiltin()) {
            return null;
        }
        return sr.getName();
    }
    
    public void setUserStyleName(final String styleName) {
        StyleRecord sr = this._workbook.getStyleRecord(this._index);
        if (sr == null) {
            sr = this._workbook.createStyleRecord(this._index);
        }
        if (sr.isBuiltin() && this._index <= 20) {
            throw new IllegalArgumentException("Unable to set user specified style names for built in styles!");
        }
        sr.setName(styleName);
    }
    
    @Override
    public void setShrinkToFit(final boolean shrinkToFit) {
        this._format.setShrinkToFit(shrinkToFit);
    }
    
    @Override
    public boolean getShrinkToFit() {
        return this._format.getShrinkToFit();
    }
    
    public short getReadingOrder() {
        return this._format.getReadingOrder();
    }
    
    public void setReadingOrder(final short order) {
        this._format.setReadingOrder(order);
    }
    
    public void verifyBelongsToWorkbook(final HSSFWorkbook wb) {
        if (wb.getWorkbook() != this._workbook) {
            throw new IllegalArgumentException("This Style does not belong to the supplied Workbook. Are you trying to assign a style from one workbook to the cell of a differnt workbook?");
        }
    }
    
    @Override
    public void cloneStyleFrom(final CellStyle source) {
        if (source instanceof HSSFCellStyle) {
            this.cloneStyleFrom((HSSFCellStyle)source);
            return;
        }
        throw new IllegalArgumentException("Can only clone from one HSSFCellStyle to another, not between HSSFCellStyle and XSSFCellStyle");
    }
    
    public void cloneStyleFrom(final HSSFCellStyle source) {
        this._format.cloneStyleFrom(source._format);
        if (this._workbook != source._workbook) {
            HSSFCellStyle.lastDateFormat.set((Short)(-32768));
            HSSFCellStyle.lastFormats.set(null);
            HSSFCellStyle.getDataFormatStringCache.set(null);
            final short fmt = (short)this._workbook.createFormat(source.getDataFormatString());
            this.setDataFormat(fmt);
            final FontRecord fr = this._workbook.createNewFont();
            fr.cloneStyleFrom(source._workbook.getFontRecordAt(source.getFontIndex()));
            final HSSFFont font = new HSSFFont((short)this._workbook.getFontIndex(fr), fr);
            this.setFont(font);
        }
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + ((this._format == null) ? 0 : this._format.hashCode());
        result = 31 * result + this._index;
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
        if (obj instanceof HSSFCellStyle) {
            final HSSFCellStyle other = (HSSFCellStyle)obj;
            if (this._format == null) {
                if (other._format != null) {
                    return false;
                }
            }
            else if (!this._format.equals(other._format)) {
                return false;
            }
            return this._index == other._index;
        }
        return false;
    }
    
    static {
        HSSFCellStyle.lastDateFormat = new ThreadLocal<Short>() {
            @Override
            protected Short initialValue() {
                return -32768;
            }
        };
        HSSFCellStyle.lastFormats = new ThreadLocal<List<FormatRecord>>();
        HSSFCellStyle.getDataFormatStringCache = new ThreadLocal<String>();
    }
}
