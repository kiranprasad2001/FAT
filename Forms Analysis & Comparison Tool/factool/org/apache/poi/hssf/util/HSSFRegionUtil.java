// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.util;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.Region;

public final class HSSFRegionUtil
{
    private HSSFRegionUtil() {
    }
    
    @Deprecated
    private static CellRangeAddress toCRA(final Region region) {
        return Region.convertToCellRangeAddress(region);
    }
    
    @Deprecated
    public static void setBorderLeft(final short border, final org.apache.poi.hssf.util.Region region, final HSSFSheet sheet, final HSSFWorkbook workbook) {
        setBorderLeft(border, toCRA(region), sheet, workbook);
    }
    
    public static void setBorderLeft(final int border, final CellRangeAddress region, final HSSFSheet sheet, final HSSFWorkbook workbook) {
        RegionUtil.setBorderLeft(border, region, sheet, workbook);
    }
    
    @Deprecated
    public static void setLeftBorderColor(final short color, final org.apache.poi.hssf.util.Region region, final HSSFSheet sheet, final HSSFWorkbook workbook) {
        setLeftBorderColor(color, toCRA(region), sheet, workbook);
    }
    
    public static void setLeftBorderColor(final int color, final CellRangeAddress region, final HSSFSheet sheet, final HSSFWorkbook workbook) {
        RegionUtil.setLeftBorderColor(color, region, sheet, workbook);
    }
    
    @Deprecated
    public static void setBorderRight(final short border, final org.apache.poi.hssf.util.Region region, final HSSFSheet sheet, final HSSFWorkbook workbook) {
        setBorderRight(border, toCRA(region), sheet, workbook);
    }
    
    public static void setBorderRight(final int border, final CellRangeAddress region, final HSSFSheet sheet, final HSSFWorkbook workbook) {
        RegionUtil.setBorderRight(border, region, sheet, workbook);
    }
    
    @Deprecated
    public static void setRightBorderColor(final short color, final org.apache.poi.hssf.util.Region region, final HSSFSheet sheet, final HSSFWorkbook workbook) {
        setRightBorderColor(color, toCRA(region), sheet, workbook);
    }
    
    public static void setRightBorderColor(final int color, final CellRangeAddress region, final HSSFSheet sheet, final HSSFWorkbook workbook) {
        RegionUtil.setRightBorderColor(color, region, sheet, workbook);
    }
    
    @Deprecated
    public static void setBorderBottom(final short border, final org.apache.poi.hssf.util.Region region, final HSSFSheet sheet, final HSSFWorkbook workbook) {
        setBorderBottom(border, toCRA(region), sheet, workbook);
    }
    
    public static void setBorderBottom(final int border, final CellRangeAddress region, final HSSFSheet sheet, final HSSFWorkbook workbook) {
        RegionUtil.setBorderBottom(border, region, sheet, workbook);
    }
    
    @Deprecated
    public static void setBottomBorderColor(final short color, final org.apache.poi.hssf.util.Region region, final HSSFSheet sheet, final HSSFWorkbook workbook) {
        setBottomBorderColor(color, toCRA(region), sheet, workbook);
    }
    
    public static void setBottomBorderColor(final int color, final CellRangeAddress region, final HSSFSheet sheet, final HSSFWorkbook workbook) {
        RegionUtil.setBottomBorderColor(color, region, sheet, workbook);
    }
    
    @Deprecated
    public static void setBorderTop(final short border, final org.apache.poi.hssf.util.Region region, final HSSFSheet sheet, final HSSFWorkbook workbook) {
        setBorderTop(border, toCRA(region), sheet, workbook);
    }
    
    public static void setBorderTop(final int border, final CellRangeAddress region, final HSSFSheet sheet, final HSSFWorkbook workbook) {
        RegionUtil.setBorderTop(border, region, sheet, workbook);
    }
    
    @Deprecated
    public static void setTopBorderColor(final short color, final org.apache.poi.hssf.util.Region region, final HSSFSheet sheet, final HSSFWorkbook workbook) {
        setTopBorderColor(color, toCRA(region), sheet, workbook);
    }
    
    public static void setTopBorderColor(final int color, final CellRangeAddress region, final HSSFSheet sheet, final HSSFWorkbook workbook) {
        RegionUtil.setTopBorderColor(color, region, sheet, workbook);
    }
}
