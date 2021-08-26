// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

import java.util.List;
import org.apache.poi.hssf.util.PaneInformation;
import java.util.Iterator;
import org.apache.poi.ss.util.CellRangeAddress;

public interface Sheet extends Iterable<Row>
{
    public static final short LeftMargin = 0;
    public static final short RightMargin = 1;
    public static final short TopMargin = 2;
    public static final short BottomMargin = 3;
    public static final short HeaderMargin = 4;
    public static final short FooterMargin = 5;
    public static final byte PANE_LOWER_RIGHT = 0;
    public static final byte PANE_UPPER_RIGHT = 1;
    public static final byte PANE_LOWER_LEFT = 2;
    public static final byte PANE_UPPER_LEFT = 3;
    
    Row createRow(final int p0);
    
    void removeRow(final Row p0);
    
    Row getRow(final int p0);
    
    int getPhysicalNumberOfRows();
    
    int getFirstRowNum();
    
    int getLastRowNum();
    
    void setColumnHidden(final int p0, final boolean p1);
    
    boolean isColumnHidden(final int p0);
    
    void setRightToLeft(final boolean p0);
    
    boolean isRightToLeft();
    
    void setColumnWidth(final int p0, final int p1);
    
    int getColumnWidth(final int p0);
    
    float getColumnWidthInPixels(final int p0);
    
    void setDefaultColumnWidth(final int p0);
    
    int getDefaultColumnWidth();
    
    short getDefaultRowHeight();
    
    float getDefaultRowHeightInPoints();
    
    void setDefaultRowHeight(final short p0);
    
    void setDefaultRowHeightInPoints(final float p0);
    
    CellStyle getColumnStyle(final int p0);
    
    int addMergedRegion(final CellRangeAddress p0);
    
    void setVerticallyCenter(final boolean p0);
    
    void setHorizontallyCenter(final boolean p0);
    
    boolean getHorizontallyCenter();
    
    boolean getVerticallyCenter();
    
    void removeMergedRegion(final int p0);
    
    int getNumMergedRegions();
    
    CellRangeAddress getMergedRegion(final int p0);
    
    Iterator<Row> rowIterator();
    
    void setForceFormulaRecalculation(final boolean p0);
    
    boolean getForceFormulaRecalculation();
    
    void setAutobreaks(final boolean p0);
    
    void setDisplayGuts(final boolean p0);
    
    void setDisplayZeros(final boolean p0);
    
    boolean isDisplayZeros();
    
    void setFitToPage(final boolean p0);
    
    void setRowSumsBelow(final boolean p0);
    
    void setRowSumsRight(final boolean p0);
    
    boolean getAutobreaks();
    
    boolean getDisplayGuts();
    
    boolean getFitToPage();
    
    boolean getRowSumsBelow();
    
    boolean getRowSumsRight();
    
    boolean isPrintGridlines();
    
    void setPrintGridlines(final boolean p0);
    
    PrintSetup getPrintSetup();
    
    Header getHeader();
    
    Footer getFooter();
    
    void setSelected(final boolean p0);
    
    double getMargin(final short p0);
    
    void setMargin(final short p0, final double p1);
    
    boolean getProtect();
    
    void protectSheet(final String p0);
    
    boolean getScenarioProtect();
    
    void setZoom(final int p0, final int p1);
    
    short getTopRow();
    
    short getLeftCol();
    
    void showInPane(final int p0, final int p1);
    
    @Deprecated
    void showInPane(final short p0, final short p1);
    
    void shiftRows(final int p0, final int p1, final int p2);
    
    void shiftRows(final int p0, final int p1, final int p2, final boolean p3, final boolean p4);
    
    void createFreezePane(final int p0, final int p1, final int p2, final int p3);
    
    void createFreezePane(final int p0, final int p1);
    
    void createSplitPane(final int p0, final int p1, final int p2, final int p3, final int p4);
    
    PaneInformation getPaneInformation();
    
    void setDisplayGridlines(final boolean p0);
    
    boolean isDisplayGridlines();
    
    void setDisplayFormulas(final boolean p0);
    
    boolean isDisplayFormulas();
    
    void setDisplayRowColHeadings(final boolean p0);
    
    boolean isDisplayRowColHeadings();
    
    void setRowBreak(final int p0);
    
    boolean isRowBroken(final int p0);
    
    void removeRowBreak(final int p0);
    
    int[] getRowBreaks();
    
    int[] getColumnBreaks();
    
    void setColumnBreak(final int p0);
    
    boolean isColumnBroken(final int p0);
    
    void removeColumnBreak(final int p0);
    
    void setColumnGroupCollapsed(final int p0, final boolean p1);
    
    void groupColumn(final int p0, final int p1);
    
    void ungroupColumn(final int p0, final int p1);
    
    void groupRow(final int p0, final int p1);
    
    void ungroupRow(final int p0, final int p1);
    
    void setRowGroupCollapsed(final int p0, final boolean p1);
    
    void setDefaultColumnStyle(final int p0, final CellStyle p1);
    
    void autoSizeColumn(final int p0);
    
    void autoSizeColumn(final int p0, final boolean p1);
    
    Comment getCellComment(final int p0, final int p1);
    
    Drawing createDrawingPatriarch();
    
    Workbook getWorkbook();
    
    String getSheetName();
    
    boolean isSelected();
    
    CellRange<? extends Cell> setArrayFormula(final String p0, final CellRangeAddress p1);
    
    CellRange<? extends Cell> removeArrayFormula(final Cell p0);
    
    DataValidationHelper getDataValidationHelper();
    
    List<? extends DataValidation> getDataValidations();
    
    void addValidationData(final DataValidation p0);
    
    AutoFilter setAutoFilter(final CellRangeAddress p0);
    
    SheetConditionalFormatting getSheetConditionalFormatting();
    
    CellRangeAddress getRepeatingRows();
    
    CellRangeAddress getRepeatingColumns();
    
    void setRepeatingRows(final CellRangeAddress p0);
    
    void setRepeatingColumns(final CellRangeAddress p0);
    
    int getColumnOutlineLevel(final int p0);
}
