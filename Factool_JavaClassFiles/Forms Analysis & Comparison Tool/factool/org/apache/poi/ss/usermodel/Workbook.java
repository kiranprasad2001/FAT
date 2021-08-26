// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

import org.apache.poi.ss.formula.udf.UDFFinder;
import java.util.List;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Closeable;

public interface Workbook extends Closeable
{
    public static final int PICTURE_TYPE_EMF = 2;
    public static final int PICTURE_TYPE_WMF = 3;
    public static final int PICTURE_TYPE_PICT = 4;
    public static final int PICTURE_TYPE_JPEG = 5;
    public static final int PICTURE_TYPE_PNG = 6;
    public static final int PICTURE_TYPE_DIB = 7;
    public static final int SHEET_STATE_VISIBLE = 0;
    public static final int SHEET_STATE_HIDDEN = 1;
    public static final int SHEET_STATE_VERY_HIDDEN = 2;
    
    int getActiveSheetIndex();
    
    void setActiveSheet(final int p0);
    
    int getFirstVisibleTab();
    
    void setFirstVisibleTab(final int p0);
    
    void setSheetOrder(final String p0, final int p1);
    
    void setSelectedTab(final int p0);
    
    void setSheetName(final int p0, final String p1);
    
    String getSheetName(final int p0);
    
    int getSheetIndex(final String p0);
    
    int getSheetIndex(final Sheet p0);
    
    Sheet createSheet();
    
    Sheet createSheet(final String p0);
    
    Sheet cloneSheet(final int p0);
    
    int getNumberOfSheets();
    
    Sheet getSheetAt(final int p0);
    
    Sheet getSheet(final String p0);
    
    void removeSheetAt(final int p0);
    
    @Deprecated
    void setRepeatingRowsAndColumns(final int p0, final int p1, final int p2, final int p3, final int p4);
    
    Font createFont();
    
    Font findFont(final short p0, final short p1, final short p2, final String p3, final boolean p4, final boolean p5, final short p6, final byte p7);
    
    short getNumberOfFonts();
    
    Font getFontAt(final short p0);
    
    CellStyle createCellStyle();
    
    short getNumCellStyles();
    
    CellStyle getCellStyleAt(final short p0);
    
    void write(final OutputStream p0) throws IOException;
    
    void close() throws IOException;
    
    int getNumberOfNames();
    
    Name getName(final String p0);
    
    Name getNameAt(final int p0);
    
    Name createName();
    
    int getNameIndex(final String p0);
    
    void removeName(final int p0);
    
    void removeName(final String p0);
    
    int linkExternalWorkbook(final String p0, final Workbook p1);
    
    void setPrintArea(final int p0, final String p1);
    
    void setPrintArea(final int p0, final int p1, final int p2, final int p3, final int p4);
    
    String getPrintArea(final int p0);
    
    void removePrintArea(final int p0);
    
    Row.MissingCellPolicy getMissingCellPolicy();
    
    void setMissingCellPolicy(final Row.MissingCellPolicy p0);
    
    DataFormat createDataFormat();
    
    int addPicture(final byte[] p0, final int p1);
    
    List<? extends PictureData> getAllPictures();
    
    CreationHelper getCreationHelper();
    
    boolean isHidden();
    
    void setHidden(final boolean p0);
    
    boolean isSheetHidden(final int p0);
    
    boolean isSheetVeryHidden(final int p0);
    
    void setSheetHidden(final int p0, final boolean p1);
    
    void setSheetHidden(final int p0, final int p1);
    
    void addToolPack(final UDFFinder p0);
    
    void setForceFormulaRecalculation(final boolean p0);
    
    boolean getForceFormulaRecalculation();
}
