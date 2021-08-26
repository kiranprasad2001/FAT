// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;

public final class RegionUtil
{
    private RegionUtil() {
    }
    
    public static void setBorderLeft(final int border, final CellRangeAddress region, final Sheet sheet, final Workbook workbook) {
        final int rowStart = region.getFirstRow();
        final int rowEnd = region.getLastRow();
        final int column = region.getFirstColumn();
        final CellPropertySetter cps = new CellPropertySetter(workbook, "borderLeft", border);
        for (int i = rowStart; i <= rowEnd; ++i) {
            cps.setProperty(CellUtil.getRow(i, sheet), column);
        }
    }
    
    public static void setLeftBorderColor(final int color, final CellRangeAddress region, final Sheet sheet, final Workbook workbook) {
        final int rowStart = region.getFirstRow();
        final int rowEnd = region.getLastRow();
        final int column = region.getFirstColumn();
        final CellPropertySetter cps = new CellPropertySetter(workbook, "leftBorderColor", color);
        for (int i = rowStart; i <= rowEnd; ++i) {
            cps.setProperty(CellUtil.getRow(i, sheet), column);
        }
    }
    
    public static void setBorderRight(final int border, final CellRangeAddress region, final Sheet sheet, final Workbook workbook) {
        final int rowStart = region.getFirstRow();
        final int rowEnd = region.getLastRow();
        final int column = region.getLastColumn();
        final CellPropertySetter cps = new CellPropertySetter(workbook, "borderRight", border);
        for (int i = rowStart; i <= rowEnd; ++i) {
            cps.setProperty(CellUtil.getRow(i, sheet), column);
        }
    }
    
    public static void setRightBorderColor(final int color, final CellRangeAddress region, final Sheet sheet, final Workbook workbook) {
        final int rowStart = region.getFirstRow();
        final int rowEnd = region.getLastRow();
        final int column = region.getLastColumn();
        final CellPropertySetter cps = new CellPropertySetter(workbook, "rightBorderColor", color);
        for (int i = rowStart; i <= rowEnd; ++i) {
            cps.setProperty(CellUtil.getRow(i, sheet), column);
        }
    }
    
    public static void setBorderBottom(final int border, final CellRangeAddress region, final Sheet sheet, final Workbook workbook) {
        final int colStart = region.getFirstColumn();
        final int colEnd = region.getLastColumn();
        final int rowIndex = region.getLastRow();
        final CellPropertySetter cps = new CellPropertySetter(workbook, "borderBottom", border);
        final Row row = CellUtil.getRow(rowIndex, sheet);
        for (int i = colStart; i <= colEnd; ++i) {
            cps.setProperty(row, i);
        }
    }
    
    public static void setBottomBorderColor(final int color, final CellRangeAddress region, final Sheet sheet, final Workbook workbook) {
        final int colStart = region.getFirstColumn();
        final int colEnd = region.getLastColumn();
        final int rowIndex = region.getLastRow();
        final CellPropertySetter cps = new CellPropertySetter(workbook, "bottomBorderColor", color);
        final Row row = CellUtil.getRow(rowIndex, sheet);
        for (int i = colStart; i <= colEnd; ++i) {
            cps.setProperty(row, i);
        }
    }
    
    public static void setBorderTop(final int border, final CellRangeAddress region, final Sheet sheet, final Workbook workbook) {
        final int colStart = region.getFirstColumn();
        final int colEnd = region.getLastColumn();
        final int rowIndex = region.getFirstRow();
        final CellPropertySetter cps = new CellPropertySetter(workbook, "borderTop", border);
        final Row row = CellUtil.getRow(rowIndex, sheet);
        for (int i = colStart; i <= colEnd; ++i) {
            cps.setProperty(row, i);
        }
    }
    
    public static void setTopBorderColor(final int color, final CellRangeAddress region, final Sheet sheet, final Workbook workbook) {
        final int colStart = region.getFirstColumn();
        final int colEnd = region.getLastColumn();
        final int rowIndex = region.getFirstRow();
        final CellPropertySetter cps = new CellPropertySetter(workbook, "topBorderColor", color);
        final Row row = CellUtil.getRow(rowIndex, sheet);
        for (int i = colStart; i <= colEnd; ++i) {
            cps.setProperty(row, i);
        }
    }
    
    private static final class CellPropertySetter
    {
        private final Workbook _workbook;
        private final String _propertyName;
        private final Short _propertyValue;
        
        public CellPropertySetter(final Workbook workbook, final String propertyName, final int value) {
            this._workbook = workbook;
            this._propertyName = propertyName;
            this._propertyValue = (short)value;
        }
        
        public void setProperty(final Row row, final int column) {
            final Cell cell = CellUtil.getCell(row, column);
            CellUtil.setCellStyleProperty(cell, this._workbook, this._propertyName, this._propertyValue);
        }
    }
}
