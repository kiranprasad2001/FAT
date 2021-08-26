// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.formula.FormulaParseException;
import java.util.Calendar;
import java.util.Date;

public interface Cell
{
    public static final int CELL_TYPE_NUMERIC = 0;
    public static final int CELL_TYPE_STRING = 1;
    public static final int CELL_TYPE_FORMULA = 2;
    public static final int CELL_TYPE_BLANK = 3;
    public static final int CELL_TYPE_BOOLEAN = 4;
    public static final int CELL_TYPE_ERROR = 5;
    
    int getColumnIndex();
    
    int getRowIndex();
    
    Sheet getSheet();
    
    Row getRow();
    
    void setCellType(final int p0);
    
    int getCellType();
    
    int getCachedFormulaResultType();
    
    void setCellValue(final double p0);
    
    void setCellValue(final Date p0);
    
    void setCellValue(final Calendar p0);
    
    void setCellValue(final RichTextString p0);
    
    void setCellValue(final String p0);
    
    void setCellFormula(final String p0) throws FormulaParseException;
    
    String getCellFormula();
    
    double getNumericCellValue();
    
    Date getDateCellValue();
    
    RichTextString getRichStringCellValue();
    
    String getStringCellValue();
    
    void setCellValue(final boolean p0);
    
    void setCellErrorValue(final byte p0);
    
    boolean getBooleanCellValue();
    
    byte getErrorCellValue();
    
    void setCellStyle(final CellStyle p0);
    
    CellStyle getCellStyle();
    
    void setAsActiveCell();
    
    void setCellComment(final Comment p0);
    
    Comment getCellComment();
    
    void removeCellComment();
    
    Hyperlink getHyperlink();
    
    void setHyperlink(final Hyperlink p0);
    
    void removeHyperlink();
    
    CellRangeAddress getArrayFormulaRange();
    
    boolean isPartOfArrayFormulaGroup();
}
