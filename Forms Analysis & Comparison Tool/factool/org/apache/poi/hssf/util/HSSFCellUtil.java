// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.util;

import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

public final class HSSFCellUtil
{
    private HSSFCellUtil() {
    }
    
    public static HSSFRow getRow(final int rowIndex, final HSSFSheet sheet) {
        return (HSSFRow)CellUtil.getRow(rowIndex, sheet);
    }
    
    public static HSSFCell getCell(final HSSFRow row, final int columnIndex) {
        return (HSSFCell)CellUtil.getCell(row, columnIndex);
    }
    
    public static HSSFCell createCell(final HSSFRow row, final int column, final String value, final HSSFCellStyle style) {
        return (HSSFCell)CellUtil.createCell(row, column, value, style);
    }
    
    public static HSSFCell createCell(final HSSFRow row, final int column, final String value) {
        return createCell(row, column, value, null);
    }
    
    public static void setAlignment(final HSSFCell cell, final HSSFWorkbook workbook, final short align) {
        CellUtil.setAlignment(cell, workbook, align);
    }
    
    public static void setFont(final HSSFCell cell, final HSSFWorkbook workbook, final HSSFFont font) {
        CellUtil.setFont(cell, workbook, font);
    }
    
    public static void setCellStyleProperty(final HSSFCell cell, final HSSFWorkbook workbook, final String propertyName, final Object propertyValue) {
        CellUtil.setCellStyleProperty(cell, workbook, propertyName, propertyValue);
    }
    
    public static HSSFCell translateUnicodeValues(final HSSFCell cell) {
        CellUtil.translateUnicodeValues(cell);
        return cell;
    }
}
