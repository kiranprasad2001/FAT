// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.util;

import java.util.Map;
import org.apache.poi.ss.usermodel.CellValue;
import java.text.AttributedCharacterIterator;
import java.awt.font.TextAttribute;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import java.awt.geom.AffineTransform;
import java.awt.font.TextLayout;
import java.text.AttributedString;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Cell;
import java.awt.font.FontRenderContext;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

public class SheetUtil
{
    private static final char defaultChar = '0';
    private static final double fontHeightMultiple = 2.0;
    private static final FormulaEvaluator dummyEvaluator;
    private static final FontRenderContext fontRenderContext;
    
    public static double getCellWidth(Cell cell, final int defaultCharWidth, final DataFormatter formatter, final boolean useMergedCells) {
        final Sheet sheet = cell.getSheet();
        final Workbook wb = sheet.getWorkbook();
        final Row row = cell.getRow();
        final int column = cell.getColumnIndex();
        int colspan = 1;
        for (int i = 0; i < sheet.getNumMergedRegions(); ++i) {
            final CellRangeAddress region = sheet.getMergedRegion(i);
            if (containsCell(region, row.getRowNum(), column)) {
                if (!useMergedCells) {
                    return -1.0;
                }
                cell = row.getCell(region.getFirstColumn());
                colspan = 1 + region.getLastColumn() - region.getFirstColumn();
            }
        }
        final CellStyle style = cell.getCellStyle();
        int cellType = cell.getCellType();
        if (cellType == 2) {
            cellType = cell.getCachedFormulaResultType();
        }
        final Font font = wb.getFontAt(style.getFontIndex());
        double width = -1.0;
        if (cellType == 1) {
            final RichTextString rt = cell.getRichStringCellValue();
            final String[] lines = rt.getString().split("\\n");
            for (int j = 0; j < lines.length; ++j) {
                final String txt = lines[j] + '0';
                final AttributedString str = new AttributedString(txt);
                copyAttributes(font, str, 0, txt.length());
                if (rt.numFormattingRuns() > 0) {}
                final TextLayout layout = new TextLayout(str.getIterator(), SheetUtil.fontRenderContext);
                if (style.getRotation() != 0) {
                    final AffineTransform trans = new AffineTransform();
                    trans.concatenate(AffineTransform.getRotateInstance(style.getRotation() * 2.0 * 3.141592653589793 / 360.0));
                    trans.concatenate(AffineTransform.getScaleInstance(1.0, 2.0));
                    width = Math.max(width, layout.getOutline(trans).getBounds().getWidth() / colspan / defaultCharWidth + cell.getCellStyle().getIndention());
                }
                else {
                    width = Math.max(width, layout.getBounds().getWidth() / colspan / defaultCharWidth + cell.getCellStyle().getIndention());
                }
            }
        }
        else {
            String sval = null;
            if (cellType == 0) {
                try {
                    sval = formatter.formatCellValue(cell, SheetUtil.dummyEvaluator);
                }
                catch (Exception e) {
                    sval = String.valueOf(cell.getNumericCellValue());
                }
            }
            else if (cellType == 4) {
                sval = String.valueOf(cell.getBooleanCellValue()).toUpperCase();
            }
            if (sval != null) {
                final String txt2 = sval + '0';
                final AttributedString str = new AttributedString(txt2);
                copyAttributes(font, str, 0, txt2.length());
                final TextLayout layout = new TextLayout(str.getIterator(), SheetUtil.fontRenderContext);
                if (style.getRotation() != 0) {
                    final AffineTransform trans2 = new AffineTransform();
                    trans2.concatenate(AffineTransform.getRotateInstance(style.getRotation() * 2.0 * 3.141592653589793 / 360.0));
                    trans2.concatenate(AffineTransform.getScaleInstance(1.0, 2.0));
                    width = Math.max(width, layout.getOutline(trans2).getBounds().getWidth() / colspan / defaultCharWidth + cell.getCellStyle().getIndention());
                }
                else {
                    width = Math.max(width, layout.getBounds().getWidth() / colspan / defaultCharWidth + cell.getCellStyle().getIndention());
                }
            }
        }
        return width;
    }
    
    public static double getColumnWidth(final Sheet sheet, final int column, final boolean useMergedCells) {
        final Workbook wb = sheet.getWorkbook();
        final DataFormatter formatter = new DataFormatter();
        final Font defaultFont = wb.getFontAt((short)0);
        final AttributedString str = new AttributedString(String.valueOf('0'));
        copyAttributes(defaultFont, str, 0, 1);
        final TextLayout layout = new TextLayout(str.getIterator(), SheetUtil.fontRenderContext);
        final int defaultCharWidth = (int)layout.getAdvance();
        double width = -1.0;
        for (final Row row : sheet) {
            final Cell cell = row.getCell(column);
            if (cell == null) {
                continue;
            }
            final double cellWidth = getCellWidth(cell, defaultCharWidth, formatter, useMergedCells);
            width = Math.max(width, cellWidth);
        }
        return width;
    }
    
    public static double getColumnWidth(final Sheet sheet, final int column, final boolean useMergedCells, final int firstRow, final int lastRow) {
        final Workbook wb = sheet.getWorkbook();
        final DataFormatter formatter = new DataFormatter();
        final Font defaultFont = wb.getFontAt((short)0);
        final AttributedString str = new AttributedString(String.valueOf('0'));
        copyAttributes(defaultFont, str, 0, 1);
        final TextLayout layout = new TextLayout(str.getIterator(), SheetUtil.fontRenderContext);
        final int defaultCharWidth = (int)layout.getAdvance();
        double width = -1.0;
        for (int rowIdx = firstRow; rowIdx <= lastRow; ++rowIdx) {
            final Row row = sheet.getRow(rowIdx);
            if (row != null) {
                final Cell cell = row.getCell(column);
                if (cell != null) {
                    final double cellWidth = getCellWidth(cell, defaultCharWidth, formatter, useMergedCells);
                    width = Math.max(width, cellWidth);
                }
            }
        }
        return width;
    }
    
    private static void copyAttributes(final Font font, final AttributedString str, final int startIdx, final int endIdx) {
        str.addAttribute(TextAttribute.FAMILY, font.getFontName(), startIdx, endIdx);
        str.addAttribute(TextAttribute.SIZE, (float)font.getFontHeightInPoints());
        if (font.getBoldweight() == 700) {
            str.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, startIdx, endIdx);
        }
        if (font.getItalic()) {
            str.addAttribute(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE, startIdx, endIdx);
        }
        if (font.getUnderline() == 1) {
            str.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON, startIdx, endIdx);
        }
    }
    
    public static boolean containsCell(final CellRangeAddress cr, final int rowIx, final int colIx) {
        return cr.getFirstRow() <= rowIx && cr.getLastRow() >= rowIx && cr.getFirstColumn() <= colIx && cr.getLastColumn() >= colIx;
    }
    
    public static Cell getCellWithMerges(final Sheet sheet, final int rowIx, final int colIx) {
        Row r = sheet.getRow(rowIx);
        if (r != null) {
            final Cell c = r.getCell(colIx);
            if (c != null) {
                return c;
            }
        }
        for (int mr = 0; mr < sheet.getNumMergedRegions(); ++mr) {
            final CellRangeAddress mergedRegion = sheet.getMergedRegion(mr);
            if (mergedRegion.isInRange(rowIx, colIx)) {
                r = sheet.getRow(mergedRegion.getFirstRow());
                if (r != null) {
                    return r.getCell(mergedRegion.getFirstColumn());
                }
            }
        }
        return null;
    }
    
    static {
        dummyEvaluator = new FormulaEvaluator() {
            @Override
            public void clearAllCachedResultValues() {
            }
            
            @Override
            public void notifySetFormula(final Cell cell) {
            }
            
            @Override
            public void notifyDeleteCell(final Cell cell) {
            }
            
            @Override
            public void notifyUpdateCell(final Cell cell) {
            }
            
            @Override
            public CellValue evaluate(final Cell cell) {
                return null;
            }
            
            @Override
            public Cell evaluateInCell(final Cell cell) {
                return null;
            }
            
            @Override
            public void setupReferencedWorkbooks(final Map<String, FormulaEvaluator> workbooks) {
            }
            
            @Override
            public void setDebugEvaluationOutputForNextEval(final boolean value) {
            }
            
            @Override
            public void setIgnoreMissingWorkbooks(final boolean ignore) {
            }
            
            @Override
            public void evaluateAll() {
            }
            
            @Override
            public int evaluateFormulaCell(final Cell cell) {
                return cell.getCachedFormulaResultType();
            }
        };
        fontRenderContext = new FontRenderContext(null, true, true);
    }
}
