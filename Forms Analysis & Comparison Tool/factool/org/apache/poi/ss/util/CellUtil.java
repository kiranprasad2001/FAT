// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.util;

import java.util.HashMap;
import java.util.Map;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public final class CellUtil
{
    public static final String ALIGNMENT = "alignment";
    public static final String BORDER_BOTTOM = "borderBottom";
    public static final String BORDER_LEFT = "borderLeft";
    public static final String BORDER_RIGHT = "borderRight";
    public static final String BORDER_TOP = "borderTop";
    public static final String BOTTOM_BORDER_COLOR = "bottomBorderColor";
    public static final String DATA_FORMAT = "dataFormat";
    public static final String FILL_BACKGROUND_COLOR = "fillBackgroundColor";
    public static final String FILL_FOREGROUND_COLOR = "fillForegroundColor";
    public static final String FILL_PATTERN = "fillPattern";
    public static final String FONT = "font";
    public static final String HIDDEN = "hidden";
    public static final String INDENTION = "indention";
    public static final String LEFT_BORDER_COLOR = "leftBorderColor";
    public static final String LOCKED = "locked";
    public static final String RIGHT_BORDER_COLOR = "rightBorderColor";
    public static final String ROTATION = "rotation";
    public static final String TOP_BORDER_COLOR = "topBorderColor";
    public static final String VERTICAL_ALIGNMENT = "verticalAlignment";
    public static final String WRAP_TEXT = "wrapText";
    private static UnicodeMapping[] unicodeMappings;
    
    private CellUtil() {
    }
    
    public static Row getRow(final int rowIndex, final Sheet sheet) {
        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }
        return row;
    }
    
    public static Cell getCell(final Row row, final int columnIndex) {
        Cell cell = row.getCell(columnIndex);
        if (cell == null) {
            cell = row.createCell(columnIndex);
        }
        return cell;
    }
    
    public static Cell createCell(final Row row, final int column, final String value, final CellStyle style) {
        final Cell cell = getCell(row, column);
        cell.setCellValue(cell.getRow().getSheet().getWorkbook().getCreationHelper().createRichTextString(value));
        if (style != null) {
            cell.setCellStyle(style);
        }
        return cell;
    }
    
    public static Cell createCell(final Row row, final int column, final String value) {
        return createCell(row, column, value, null);
    }
    
    public static void setAlignment(final Cell cell, final Workbook workbook, final short align) {
        setCellStyleProperty(cell, workbook, "alignment", align);
    }
    
    public static void setFont(final Cell cell, final Workbook workbook, final Font font) {
        setCellStyleProperty(cell, workbook, "font", font.getIndex());
    }
    
    public static void setCellStyleProperty(final Cell cell, final Workbook workbook, final String propertyName, final Object propertyValue) {
        final CellStyle originalStyle = cell.getCellStyle();
        CellStyle newStyle = null;
        final Map<String, Object> values = getFormatProperties(originalStyle);
        values.put(propertyName, propertyValue);
        for (short numberCellStyles = workbook.getNumCellStyles(), i = 0; i < numberCellStyles; ++i) {
            final CellStyle wbStyle = workbook.getCellStyleAt(i);
            final Map<String, Object> wbStyleMap = getFormatProperties(wbStyle);
            if (wbStyleMap.equals(values)) {
                newStyle = wbStyle;
                break;
            }
        }
        if (newStyle == null) {
            newStyle = workbook.createCellStyle();
            setFormatProperties(newStyle, workbook, values);
        }
        cell.setCellStyle(newStyle);
    }
    
    private static Map<String, Object> getFormatProperties(final CellStyle style) {
        final Map<String, Object> properties = new HashMap<String, Object>();
        putShort(properties, "alignment", style.getAlignment());
        putShort(properties, "borderBottom", style.getBorderBottom());
        putShort(properties, "borderLeft", style.getBorderLeft());
        putShort(properties, "borderRight", style.getBorderRight());
        putShort(properties, "borderTop", style.getBorderTop());
        putShort(properties, "bottomBorderColor", style.getBottomBorderColor());
        putShort(properties, "dataFormat", style.getDataFormat());
        putShort(properties, "fillBackgroundColor", style.getFillBackgroundColor());
        putShort(properties, "fillForegroundColor", style.getFillForegroundColor());
        putShort(properties, "fillPattern", style.getFillPattern());
        putShort(properties, "font", style.getFontIndex());
        putBoolean(properties, "hidden", style.getHidden());
        putShort(properties, "indention", style.getIndention());
        putShort(properties, "leftBorderColor", style.getLeftBorderColor());
        putBoolean(properties, "locked", style.getLocked());
        putShort(properties, "rightBorderColor", style.getRightBorderColor());
        putShort(properties, "rotation", style.getRotation());
        putShort(properties, "topBorderColor", style.getTopBorderColor());
        putShort(properties, "verticalAlignment", style.getVerticalAlignment());
        putBoolean(properties, "wrapText", style.getWrapText());
        return properties;
    }
    
    private static void setFormatProperties(final CellStyle style, final Workbook workbook, final Map<String, Object> properties) {
        style.setAlignment(getShort(properties, "alignment"));
        style.setBorderBottom(getShort(properties, "borderBottom"));
        style.setBorderLeft(getShort(properties, "borderLeft"));
        style.setBorderRight(getShort(properties, "borderRight"));
        style.setBorderTop(getShort(properties, "borderTop"));
        style.setBottomBorderColor(getShort(properties, "bottomBorderColor"));
        style.setDataFormat(getShort(properties, "dataFormat"));
        style.setFillBackgroundColor(getShort(properties, "fillBackgroundColor"));
        style.setFillForegroundColor(getShort(properties, "fillForegroundColor"));
        style.setFillPattern(getShort(properties, "fillPattern"));
        style.setFont(workbook.getFontAt(getShort(properties, "font")));
        style.setHidden(getBoolean(properties, "hidden"));
        style.setIndention(getShort(properties, "indention"));
        style.setLeftBorderColor(getShort(properties, "leftBorderColor"));
        style.setLocked(getBoolean(properties, "locked"));
        style.setRightBorderColor(getShort(properties, "rightBorderColor"));
        style.setRotation(getShort(properties, "rotation"));
        style.setTopBorderColor(getShort(properties, "topBorderColor"));
        style.setVerticalAlignment(getShort(properties, "verticalAlignment"));
        style.setWrapText(getBoolean(properties, "wrapText"));
    }
    
    private static short getShort(final Map<String, Object> properties, final String name) {
        final Object value = properties.get(name);
        if (value instanceof Short) {
            return (short)value;
        }
        return 0;
    }
    
    private static boolean getBoolean(final Map<String, Object> properties, final String name) {
        final Object value = properties.get(name);
        return value instanceof Boolean && (boolean)value;
    }
    
    private static void putShort(final Map<String, Object> properties, final String name, final short value) {
        properties.put(name, value);
    }
    
    private static void putBoolean(final Map<String, Object> properties, final String name, final boolean value) {
        properties.put(name, value);
    }
    
    public static Cell translateUnicodeValues(final Cell cell) {
        String s = cell.getRichStringCellValue().getString();
        boolean foundUnicode = false;
        final String lowerCaseStr = s.toLowerCase();
        for (int i = 0; i < CellUtil.unicodeMappings.length; ++i) {
            final UnicodeMapping entry = CellUtil.unicodeMappings[i];
            final String key = entry.entityName;
            if (lowerCaseStr.indexOf(key) != -1) {
                s = s.replaceAll(key, entry.resolvedValue);
                foundUnicode = true;
            }
        }
        if (foundUnicode) {
            cell.setCellValue(cell.getRow().getSheet().getWorkbook().getCreationHelper().createRichTextString(s));
        }
        return cell;
    }
    
    private static UnicodeMapping um(final String entityName, final String resolvedValue) {
        return new UnicodeMapping(entityName, resolvedValue);
    }
    
    static {
        CellUtil.unicodeMappings = new UnicodeMapping[] { um("alpha", "\u03b1"), um("beta", "\u03b2"), um("gamma", "\u03b3"), um("delta", "\u03b4"), um("epsilon", "\u03b5"), um("zeta", "\u03b6"), um("eta", "\u03b7"), um("theta", "\u03b8"), um("iota", "\u03b9"), um("kappa", "\u03ba"), um("lambda", "\u03bb"), um("mu", "\u03bc"), um("nu", "\u03bd"), um("xi", "\u03be"), um("omicron", "\u03bf") };
    }
    
    private static final class UnicodeMapping
    {
        public final String entityName;
        public final String resolvedValue;
        
        public UnicodeMapping(final String pEntityName, final String pResolvedValue) {
            this.entityName = "&" + pEntityName + ";";
            this.resolvedValue = pResolvedValue;
        }
    }
}
