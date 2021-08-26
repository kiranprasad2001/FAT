// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import org.apache.poi.util.Configurator;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.AutoFilter;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import org.apache.poi.ss.formula.ptg.UnionPtg;
import org.apache.poi.ss.formula.ptg.MemFuncPtg;
import org.apache.poi.hssf.record.NameRecord;
import org.apache.poi.hssf.record.AutoFilterInfoRecord;
import org.apache.poi.ss.formula.ptg.Area3DPtg;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.hssf.record.aggregates.FormulaRecordAggregate;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.ss.util.SSCellRange;
import org.apache.poi.ss.usermodel.CellRange;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.hssf.model.DrawingManager2;
import org.apache.poi.ddf.EscherRecord;
import java.io.OutputStream;
import java.io.PrintWriter;
import org.apache.poi.hssf.record.EscherAggregate;
import org.apache.poi.hssf.util.PaneInformation;
import org.apache.poi.hssf.record.RecordBase;
import java.util.Collection;
import org.apache.poi.ss.formula.FormulaShifter;
import org.apache.poi.hssf.record.NoteRecord;
import org.apache.poi.ss.util.SheetUtil;
import org.apache.poi.hssf.record.SCLRecord;
import org.apache.poi.hssf.record.aggregates.WorksheetProtectionBlock;
import org.apache.poi.hssf.record.WSBoolRecord;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.Region;
import org.apache.poi.hssf.record.ExtendedFormatRecord;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.hssf.record.aggregates.DataValidityTable;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.formula.FormulaRenderingWorkbook;
import org.apache.poi.hssf.record.DVRecord;
import org.apache.poi.hssf.record.aggregates.RecordAggregate;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import java.util.Iterator;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RowRecord;
import org.apache.poi.hssf.record.CellValueRecordInterface;
import org.apache.poi.hssf.record.DrawingRecord;
import org.apache.poi.hssf.model.InternalWorkbook;
import java.util.TreeMap;
import org.apache.poi.hssf.model.InternalSheet;
import org.apache.poi.util.POILogger;
import org.apache.poi.ss.usermodel.Sheet;

public final class HSSFSheet implements Sheet
{
    private static final POILogger log;
    private static final int DEBUG = 1;
    private static final float PX_DEFAULT = 32.0f;
    private static final float PX_MODIFIED = 36.56f;
    public static final int INITIAL_CAPACITY;
    private final InternalSheet _sheet;
    private final TreeMap<Integer, HSSFRow> _rows;
    protected final InternalWorkbook _book;
    protected final HSSFWorkbook _workbook;
    private HSSFPatriarch _patriarch;
    private int _firstrow;
    private int _lastrow;
    
    protected HSSFSheet(final HSSFWorkbook workbook) {
        this._sheet = InternalSheet.createSheet();
        this._rows = new TreeMap<Integer, HSSFRow>();
        this._workbook = workbook;
        this._book = workbook.getWorkbook();
    }
    
    protected HSSFSheet(final HSSFWorkbook workbook, final InternalSheet sheet) {
        this._sheet = sheet;
        this._rows = new TreeMap<Integer, HSSFRow>();
        this._workbook = workbook;
        this._book = workbook.getWorkbook();
        this.setPropertiesFromSheet(sheet);
    }
    
    HSSFSheet cloneSheet(final HSSFWorkbook workbook) {
        this.getDrawingPatriarch();
        final HSSFSheet sheet = new HSSFSheet(workbook, this._sheet.cloneSheet());
        final int pos = sheet._sheet.findFirstRecordLocBySid((short)236);
        final DrawingRecord dr = (DrawingRecord)sheet._sheet.findFirstRecordBySid((short)236);
        if (null != dr) {
            sheet._sheet.getRecords().remove(dr);
        }
        if (this.getDrawingPatriarch() != null) {
            final HSSFPatriarch patr = HSSFPatriarch.createPatriarch(this.getDrawingPatriarch(), sheet);
            sheet._sheet.getRecords().add(pos, patr._getBoundAggregate());
            sheet._patriarch = patr;
        }
        return sheet;
    }
    
    protected void preSerialize() {
        if (this._patriarch != null) {
            this._patriarch.preSerialize();
        }
    }
    
    @Override
    public HSSFWorkbook getWorkbook() {
        return this._workbook;
    }
    
    private void setPropertiesFromSheet(final InternalSheet sheet) {
        RowRecord row = sheet.getNextRow();
        final boolean rowRecordsAlreadyPresent = row != null;
        while (row != null) {
            this.createRowFromRecord(row);
            row = sheet.getNextRow();
        }
        final Iterator<CellValueRecordInterface> iter = sheet.getCellValueIterator();
        final long timestart = System.currentTimeMillis();
        if (HSSFSheet.log.check(1)) {
            HSSFSheet.log.log(1, "Time at start of cell creating in HSSF sheet = ", timestart);
        }
        HSSFRow lastrow = null;
        while (iter.hasNext()) {
            final CellValueRecordInterface cval = iter.next();
            final long cellstart = System.currentTimeMillis();
            HSSFRow hrow = lastrow;
            if (hrow == null || hrow.getRowNum() != cval.getRow()) {
                hrow = this.getRow(cval.getRow());
                if ((lastrow = hrow) == null) {
                    if (rowRecordsAlreadyPresent) {
                        throw new RuntimeException("Unexpected missing row when some rows already present");
                    }
                    final RowRecord rowRec = new RowRecord(cval.getRow());
                    sheet.addRow(rowRec);
                    hrow = this.createRowFromRecord(rowRec);
                }
            }
            if (HSSFSheet.log.check(1)) {
                if (cval instanceof Record) {
                    HSSFSheet.log.log(1, "record id = " + Integer.toHexString(((Record)cval).getSid()));
                }
                else {
                    HSSFSheet.log.log(1, "record = " + cval);
                }
            }
            hrow.createCellFromRecord(cval);
            if (HSSFSheet.log.check(1)) {
                HSSFSheet.log.log(1, "record took ", System.currentTimeMillis() - cellstart);
            }
        }
        if (HSSFSheet.log.check(1)) {
            HSSFSheet.log.log(1, "total sheet cell creation took ", System.currentTimeMillis() - timestart);
        }
    }
    
    @Override
    public HSSFRow createRow(final int rownum) {
        final HSSFRow row = new HSSFRow(this._workbook, this, rownum);
        row.setHeight(this.getDefaultRowHeight());
        row.getRowRecord().setBadFontHeight(false);
        this.addRow(row, true);
        return row;
    }
    
    private HSSFRow createRowFromRecord(final RowRecord row) {
        final HSSFRow hrow = new HSSFRow(this._workbook, this, row);
        this.addRow(hrow, false);
        return hrow;
    }
    
    @Override
    public void removeRow(final Row row) {
        final HSSFRow hrow = (HSSFRow)row;
        if (row.getSheet() != this) {
            throw new IllegalArgumentException("Specified row does not belong to this sheet");
        }
        for (final Cell cell : row) {
            final HSSFCell xcell = (HSSFCell)cell;
            if (xcell.isPartOfArrayFormulaGroup()) {
                final String msg = "Row[rownum=" + row.getRowNum() + "] contains cell(s) included in a multi-cell array formula. You cannot change part of an array.";
                xcell.notifyArrayFormulaChanging(msg);
            }
        }
        if (this._rows.size() > 0) {
            final Integer key = row.getRowNum();
            final HSSFRow removedRow = this._rows.remove(key);
            if (removedRow != row) {
                throw new IllegalArgumentException("Specified row does not belong to this sheet");
            }
            if (hrow.getRowNum() == this.getLastRowNum()) {
                this._lastrow = this.findLastRow(this._lastrow);
            }
            if (hrow.getRowNum() == this.getFirstRowNum()) {
                this._firstrow = this.findFirstRow(this._firstrow);
            }
            this._sheet.removeRow(hrow.getRowRecord());
        }
    }
    
    private int findLastRow(final int lastrow) {
        if (lastrow < 1) {
            return 0;
        }
        int rownum;
        HSSFRow r;
        for (rownum = lastrow - 1, r = this.getRow(rownum); r == null && rownum > 0; r = this.getRow(--rownum)) {}
        if (r == null) {
            return 0;
        }
        return rownum;
    }
    
    private int findFirstRow(final int firstrow) {
        int rownum = firstrow + 1;
        for (HSSFRow r = this.getRow(rownum); r == null && rownum <= this.getLastRowNum(); r = this.getRow(++rownum)) {}
        if (rownum > this.getLastRowNum()) {
            return 0;
        }
        return rownum;
    }
    
    private void addRow(final HSSFRow row, final boolean addLow) {
        this._rows.put(row.getRowNum(), row);
        if (addLow) {
            this._sheet.addRow(row.getRowRecord());
        }
        final boolean firstRow = this._rows.size() == 1;
        if (row.getRowNum() > this.getLastRowNum() || firstRow) {
            this._lastrow = row.getRowNum();
        }
        if (row.getRowNum() < this.getFirstRowNum() || firstRow) {
            this._firstrow = row.getRowNum();
        }
    }
    
    @Override
    public HSSFRow getRow(final int rowIndex) {
        return this._rows.get(rowIndex);
    }
    
    @Override
    public int getPhysicalNumberOfRows() {
        return this._rows.size();
    }
    
    @Override
    public int getFirstRowNum() {
        return this._firstrow;
    }
    
    @Override
    public int getLastRowNum() {
        return this._lastrow;
    }
    
    @Override
    public List<HSSFDataValidation> getDataValidations() {
        final DataValidityTable dvt = this._sheet.getOrCreateDataValidityTable();
        final List<HSSFDataValidation> hssfValidations = new ArrayList<HSSFDataValidation>();
        final RecordAggregate.RecordVisitor visitor = new RecordAggregate.RecordVisitor() {
            private HSSFEvaluationWorkbook book = HSSFEvaluationWorkbook.create(HSSFSheet.this.getWorkbook());
            
            @Override
            public void visitRecord(final Record r) {
                if (!(r instanceof DVRecord)) {
                    return;
                }
                final DVRecord dvRecord = (DVRecord)r;
                final CellRangeAddressList regions = dvRecord.getCellRangeAddress().copy();
                final DVConstraint constraint = DVConstraint.createDVConstraint(dvRecord, this.book);
                final HSSFDataValidation hssfDataValidation = new HSSFDataValidation(regions, constraint);
                hssfDataValidation.setErrorStyle(dvRecord.getErrorStyle());
                hssfDataValidation.setEmptyCellAllowed(dvRecord.getEmptyCellAllowed());
                hssfDataValidation.setSuppressDropDownArrow(dvRecord.getSuppressDropdownArrow());
                hssfDataValidation.createPromptBox(dvRecord.getPromptTitle(), dvRecord.getPromptText());
                hssfDataValidation.setShowPromptBox(dvRecord.getShowPromptOnCellSelected());
                hssfDataValidation.createErrorBox(dvRecord.getErrorTitle(), dvRecord.getErrorText());
                hssfDataValidation.setShowErrorBox(dvRecord.getShowErrorOnInvalidValue());
                hssfValidations.add(hssfDataValidation);
            }
        };
        dvt.visitContainedRecords(visitor);
        return hssfValidations;
    }
    
    @Override
    public void addValidationData(final DataValidation dataValidation) {
        if (dataValidation == null) {
            throw new IllegalArgumentException("objValidation must not be null");
        }
        final HSSFDataValidation hssfDataValidation = (HSSFDataValidation)dataValidation;
        final DataValidityTable dvt = this._sheet.getOrCreateDataValidityTable();
        final DVRecord dvRecord = hssfDataValidation.createDVRecord(this);
        dvt.addDataValidation(dvRecord);
    }
    
    @Deprecated
    public void setColumnHidden(final short columnIndex, final boolean hidden) {
        this.setColumnHidden(columnIndex & 0xFFFF, hidden);
    }
    
    @Deprecated
    public boolean isColumnHidden(final short columnIndex) {
        return this.isColumnHidden(columnIndex & 0xFFFF);
    }
    
    @Deprecated
    public void setColumnWidth(final short columnIndex, final short width) {
        this.setColumnWidth(columnIndex & 0xFFFF, width & 0xFFFF);
    }
    
    @Deprecated
    public short getColumnWidth(final short columnIndex) {
        return (short)this.getColumnWidth(columnIndex & 0xFFFF);
    }
    
    @Deprecated
    public void setDefaultColumnWidth(final short width) {
        this.setDefaultColumnWidth(width & 0xFFFF);
    }
    
    @Override
    public void setColumnHidden(final int columnIndex, final boolean hidden) {
        this._sheet.setColumnHidden(columnIndex, hidden);
    }
    
    @Override
    public boolean isColumnHidden(final int columnIndex) {
        return this._sheet.isColumnHidden(columnIndex);
    }
    
    @Override
    public void setColumnWidth(final int columnIndex, final int width) {
        this._sheet.setColumnWidth(columnIndex, width);
    }
    
    @Override
    public int getColumnWidth(final int columnIndex) {
        return this._sheet.getColumnWidth(columnIndex);
    }
    
    @Override
    public float getColumnWidthInPixels(final int column) {
        final int cw = this.getColumnWidth(column);
        final int def = this.getDefaultColumnWidth() * 256;
        final float px = (cw == def) ? 32.0f : 36.56f;
        return cw / px;
    }
    
    @Override
    public int getDefaultColumnWidth() {
        return this._sheet.getDefaultColumnWidth();
    }
    
    @Override
    public void setDefaultColumnWidth(final int width) {
        this._sheet.setDefaultColumnWidth(width);
    }
    
    @Override
    public short getDefaultRowHeight() {
        return this._sheet.getDefaultRowHeight();
    }
    
    @Override
    public float getDefaultRowHeightInPoints() {
        return this._sheet.getDefaultRowHeight() / 20.0f;
    }
    
    @Override
    public void setDefaultRowHeight(final short height) {
        this._sheet.setDefaultRowHeight(height);
    }
    
    @Override
    public void setDefaultRowHeightInPoints(final float height) {
        this._sheet.setDefaultRowHeight((short)(height * 20.0f));
    }
    
    @Override
    public HSSFCellStyle getColumnStyle(final int column) {
        final short styleIndex = this._sheet.getXFIndexForColAt((short)column);
        if (styleIndex == 15) {
            return null;
        }
        final ExtendedFormatRecord xf = this._book.getExFormatAt(styleIndex);
        return new HSSFCellStyle(styleIndex, xf, this._book);
    }
    
    public boolean isGridsPrinted() {
        return this._sheet.isGridsPrinted();
    }
    
    public void setGridsPrinted(final boolean value) {
        this._sheet.setGridsPrinted(value);
    }
    
    @Deprecated
    public int addMergedRegion(final Region region) {
        return this._sheet.addMergedRegion(region.getRowFrom(), region.getColumnFrom(), region.getRowTo(), region.getColumnTo());
    }
    
    @Override
    public int addMergedRegion(final CellRangeAddress region) {
        region.validate(SpreadsheetVersion.EXCEL97);
        this.validateArrayFormulas(region);
        return this._sheet.addMergedRegion(region.getFirstRow(), region.getFirstColumn(), region.getLastRow(), region.getLastColumn());
    }
    
    private void validateArrayFormulas(final CellRangeAddress region) {
        final int firstRow = region.getFirstRow();
        final int firstColumn = region.getFirstColumn();
        final int lastRow = region.getLastRow();
        final int lastColumn = region.getLastColumn();
        for (int rowIn = firstRow; rowIn <= lastRow; ++rowIn) {
            for (int colIn = firstColumn; colIn <= lastColumn; ++colIn) {
                final HSSFRow row = this.getRow(rowIn);
                if (row != null) {
                    final HSSFCell cell = row.getCell(colIn);
                    if (cell != null) {
                        if (cell.isPartOfArrayFormulaGroup()) {
                            final CellRangeAddress arrayRange = cell.getArrayFormulaRange();
                            if (arrayRange.getNumberOfCells() > 1 && (arrayRange.isInRange(region.getFirstRow(), region.getFirstColumn()) || arrayRange.isInRange(region.getFirstRow(), region.getFirstColumn()))) {
                                final String msg = "The range " + region.formatAsString() + " intersects with a multi-cell array formula. " + "You cannot merge cells of an array.";
                                throw new IllegalStateException(msg);
                            }
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void setForceFormulaRecalculation(final boolean value) {
        this._sheet.setUncalced(value);
    }
    
    @Override
    public boolean getForceFormulaRecalculation() {
        return this._sheet.getUncalced();
    }
    
    @Override
    public void setVerticallyCenter(final boolean value) {
        this._sheet.getPageSettings().getVCenter().setVCenter(value);
    }
    
    @Deprecated
    public boolean getVerticallyCenter(final boolean value) {
        return this.getVerticallyCenter();
    }
    
    @Override
    public boolean getVerticallyCenter() {
        return this._sheet.getPageSettings().getVCenter().getVCenter();
    }
    
    @Override
    public void setHorizontallyCenter(final boolean value) {
        this._sheet.getPageSettings().getHCenter().setHCenter(value);
    }
    
    @Override
    public boolean getHorizontallyCenter() {
        return this._sheet.getPageSettings().getHCenter().getHCenter();
    }
    
    @Override
    public void setRightToLeft(final boolean value) {
        this._sheet.getWindowTwo().setArabic(value);
    }
    
    @Override
    public boolean isRightToLeft() {
        return this._sheet.getWindowTwo().getArabic();
    }
    
    @Override
    public void removeMergedRegion(final int index) {
        this._sheet.removeMergedRegion(index);
    }
    
    @Override
    public int getNumMergedRegions() {
        return this._sheet.getNumMergedRegions();
    }
    
    @Deprecated
    public org.apache.poi.hssf.util.Region getMergedRegionAt(final int index) {
        final CellRangeAddress cra = this.getMergedRegion(index);
        return new org.apache.poi.hssf.util.Region(cra.getFirstRow(), (short)cra.getFirstColumn(), cra.getLastRow(), (short)cra.getLastColumn());
    }
    
    @Override
    public CellRangeAddress getMergedRegion(final int index) {
        return this._sheet.getMergedRegionAt(index);
    }
    
    @Override
    public Iterator<Row> rowIterator() {
        final Iterator<Row> result = (Iterator<Row>)this._rows.values().iterator();
        return result;
    }
    
    @Override
    public Iterator<Row> iterator() {
        return this.rowIterator();
    }
    
    InternalSheet getSheet() {
        return this._sheet;
    }
    
    public void setAlternativeExpression(final boolean b) {
        final WSBoolRecord record = (WSBoolRecord)this._sheet.findFirstRecordBySid((short)129);
        record.setAlternateExpression(b);
    }
    
    public void setAlternativeFormula(final boolean b) {
        final WSBoolRecord record = (WSBoolRecord)this._sheet.findFirstRecordBySid((short)129);
        record.setAlternateFormula(b);
    }
    
    @Override
    public void setAutobreaks(final boolean b) {
        final WSBoolRecord record = (WSBoolRecord)this._sheet.findFirstRecordBySid((short)129);
        record.setAutobreaks(b);
    }
    
    public void setDialog(final boolean b) {
        final WSBoolRecord record = (WSBoolRecord)this._sheet.findFirstRecordBySid((short)129);
        record.setDialog(b);
    }
    
    @Override
    public void setDisplayGuts(final boolean b) {
        final WSBoolRecord record = (WSBoolRecord)this._sheet.findFirstRecordBySid((short)129);
        record.setDisplayGuts(b);
    }
    
    @Override
    public void setFitToPage(final boolean b) {
        final WSBoolRecord record = (WSBoolRecord)this._sheet.findFirstRecordBySid((short)129);
        record.setFitToPage(b);
    }
    
    @Override
    public void setRowSumsBelow(final boolean b) {
        final WSBoolRecord record = (WSBoolRecord)this._sheet.findFirstRecordBySid((short)129);
        record.setRowSumsBelow(b);
        record.setAlternateExpression(b);
    }
    
    @Override
    public void setRowSumsRight(final boolean b) {
        final WSBoolRecord record = (WSBoolRecord)this._sheet.findFirstRecordBySid((short)129);
        record.setRowSumsRight(b);
    }
    
    public boolean getAlternateExpression() {
        return ((WSBoolRecord)this._sheet.findFirstRecordBySid((short)129)).getAlternateExpression();
    }
    
    public boolean getAlternateFormula() {
        return ((WSBoolRecord)this._sheet.findFirstRecordBySid((short)129)).getAlternateFormula();
    }
    
    @Override
    public boolean getAutobreaks() {
        return ((WSBoolRecord)this._sheet.findFirstRecordBySid((short)129)).getAutobreaks();
    }
    
    public boolean getDialog() {
        return ((WSBoolRecord)this._sheet.findFirstRecordBySid((short)129)).getDialog();
    }
    
    @Override
    public boolean getDisplayGuts() {
        return ((WSBoolRecord)this._sheet.findFirstRecordBySid((short)129)).getDisplayGuts();
    }
    
    @Override
    public boolean isDisplayZeros() {
        return this._sheet.getWindowTwo().getDisplayZeros();
    }
    
    @Override
    public void setDisplayZeros(final boolean value) {
        this._sheet.getWindowTwo().setDisplayZeros(value);
    }
    
    @Override
    public boolean getFitToPage() {
        return ((WSBoolRecord)this._sheet.findFirstRecordBySid((short)129)).getFitToPage();
    }
    
    @Override
    public boolean getRowSumsBelow() {
        return ((WSBoolRecord)this._sheet.findFirstRecordBySid((short)129)).getRowSumsBelow();
    }
    
    @Override
    public boolean getRowSumsRight() {
        return ((WSBoolRecord)this._sheet.findFirstRecordBySid((short)129)).getRowSumsRight();
    }
    
    @Override
    public boolean isPrintGridlines() {
        return this.getSheet().getPrintGridlines().getPrintGridlines();
    }
    
    @Override
    public void setPrintGridlines(final boolean newPrintGridlines) {
        this.getSheet().getPrintGridlines().setPrintGridlines(newPrintGridlines);
    }
    
    @Override
    public HSSFPrintSetup getPrintSetup() {
        return new HSSFPrintSetup(this._sheet.getPageSettings().getPrintSetup());
    }
    
    @Override
    public HSSFHeader getHeader() {
        return new HSSFHeader(this._sheet.getPageSettings());
    }
    
    @Override
    public HSSFFooter getFooter() {
        return new HSSFFooter(this._sheet.getPageSettings());
    }
    
    @Override
    public boolean isSelected() {
        return this.getSheet().getWindowTwo().getSelected();
    }
    
    @Override
    public void setSelected(final boolean sel) {
        this.getSheet().getWindowTwo().setSelected(sel);
    }
    
    public boolean isActive() {
        return this.getSheet().getWindowTwo().isActive();
    }
    
    public void setActive(final boolean sel) {
        this.getSheet().getWindowTwo().setActive(sel);
    }
    
    @Override
    public double getMargin(final short margin) {
        switch (margin) {
            case 5: {
                return this._sheet.getPageSettings().getPrintSetup().getFooterMargin();
            }
            case 4: {
                return this._sheet.getPageSettings().getPrintSetup().getHeaderMargin();
            }
            default: {
                return this._sheet.getPageSettings().getMargin(margin);
            }
        }
    }
    
    @Override
    public void setMargin(final short margin, final double size) {
        switch (margin) {
            case 5: {
                this._sheet.getPageSettings().getPrintSetup().setFooterMargin(size);
                break;
            }
            case 4: {
                this._sheet.getPageSettings().getPrintSetup().setHeaderMargin(size);
                break;
            }
            default: {
                this._sheet.getPageSettings().setMargin(margin, size);
                break;
            }
        }
    }
    
    private WorksheetProtectionBlock getProtectionBlock() {
        return this._sheet.getProtectionBlock();
    }
    
    @Override
    public boolean getProtect() {
        return this.getProtectionBlock().isSheetProtected();
    }
    
    public short getPassword() {
        return (short)this.getProtectionBlock().getPasswordHash();
    }
    
    public boolean getObjectProtect() {
        return this.getProtectionBlock().isObjectProtected();
    }
    
    @Override
    public boolean getScenarioProtect() {
        return this.getProtectionBlock().isScenarioProtected();
    }
    
    @Override
    public void protectSheet(final String password) {
        this.getProtectionBlock().protectSheet(password, true, true);
    }
    
    @Override
    public void setZoom(final int numerator, final int denominator) {
        if (numerator < 1 || numerator > 65535) {
            throw new IllegalArgumentException("Numerator must be greater than 0 and less than 65536");
        }
        if (denominator < 1 || denominator > 65535) {
            throw new IllegalArgumentException("Denominator must be greater than 0 and less than 65536");
        }
        final SCLRecord sclRecord = new SCLRecord();
        sclRecord.setNumerator((short)numerator);
        sclRecord.setDenominator((short)denominator);
        this.getSheet().setSCLRecord(sclRecord);
    }
    
    @Override
    public short getTopRow() {
        return this._sheet.getTopRow();
    }
    
    @Override
    public short getLeftCol() {
        return this._sheet.getLeftCol();
    }
    
    @Override
    public void showInPane(final int toprow, final int leftcol) {
        final int maxrow = SpreadsheetVersion.EXCEL97.getLastRowIndex();
        if (toprow > maxrow) {
            throw new IllegalArgumentException("Maximum row number is " + maxrow);
        }
        this.showInPane((short)toprow, (short)leftcol);
    }
    
    @Override
    public void showInPane(final short toprow, final short leftcol) {
        this._sheet.setTopRow(toprow);
        this._sheet.setLeftCol(leftcol);
    }
    
    protected void shiftMerged(final int startRow, final int endRow, final int n, final boolean isRow) {
        final List<CellRangeAddress> shiftedRegions = new ArrayList<CellRangeAddress>();
        for (int i = 0; i < this.getNumMergedRegions(); ++i) {
            final CellRangeAddress merged = this.getMergedRegion(i);
            final boolean inStart = merged.getFirstRow() >= startRow || merged.getLastRow() >= startRow;
            final boolean inEnd = merged.getFirstRow() <= endRow || merged.getLastRow() <= endRow;
            if (inStart) {
                if (inEnd) {
                    if (!SheetUtil.containsCell(merged, startRow - 1, 0) && !SheetUtil.containsCell(merged, endRow + 1, 0)) {
                        merged.setFirstRow(merged.getFirstRow() + n);
                        merged.setLastRow(merged.getLastRow() + n);
                        shiftedRegions.add(merged);
                        this.removeMergedRegion(i);
                        --i;
                    }
                }
            }
        }
        for (final CellRangeAddress region : shiftedRegions) {
            this.addMergedRegion(region);
        }
    }
    
    @Override
    public void shiftRows(final int startRow, final int endRow, final int n) {
        this.shiftRows(startRow, endRow, n, false, false);
    }
    
    @Override
    public void shiftRows(final int startRow, final int endRow, final int n, final boolean copyRowHeight, final boolean resetOriginalRowHeight) {
        this.shiftRows(startRow, endRow, n, copyRowHeight, resetOriginalRowHeight, true);
    }
    
    public void shiftRows(final int startRow, final int endRow, final int n, final boolean copyRowHeight, final boolean resetOriginalRowHeight, final boolean moveComments) {
        int s;
        int inc;
        if (n < 0) {
            s = startRow;
            inc = 1;
        }
        else {
            if (n <= 0) {
                return;
            }
            s = endRow;
            inc = -1;
        }
        if (moveComments) {
            final NoteRecord[] noteRecs = this._sheet.getNoteRecords();
        }
        else {
            final NoteRecord[] noteRecs = NoteRecord.EMPTY_ARRAY;
        }
        this.shiftMerged(startRow, endRow, n, true);
        this._sheet.getPageSettings().shiftRowBreaks(startRow, endRow, n);
        for (int rowNum = s; rowNum >= startRow && rowNum <= endRow && rowNum >= 0 && rowNum < 65536; rowNum += inc) {
            final HSSFRow row = this.getRow(rowNum);
            if (row != null) {
                this.notifyRowShifting(row);
            }
            HSSFRow row2Replace = this.getRow(rowNum + n);
            if (row2Replace == null) {
                row2Replace = this.createRow(rowNum + n);
            }
            row2Replace.removeAllCells();
            if (row != null) {
                if (copyRowHeight) {
                    row2Replace.setHeight(row.getHeight());
                }
                if (resetOriginalRowHeight) {
                    row.setHeight((short)255);
                }
                final Iterator<Cell> cells = row.cellIterator();
                while (cells.hasNext()) {
                    final HSSFCell cell = cells.next();
                    row.removeCell(cell);
                    final CellValueRecordInterface cellRecord = cell.getCellValueRecord();
                    cellRecord.setRow(rowNum + n);
                    row2Replace.createCellFromRecord(cellRecord);
                    this._sheet.addValueRecord(rowNum + n, cellRecord);
                    final HSSFHyperlink link = cell.getHyperlink();
                    if (link != null) {
                        link.setFirstRow(link.getFirstRow() + n);
                        link.setLastRow(link.getLastRow() + n);
                    }
                }
                row.removeAllCells();
                if (moveComments) {
                    final HSSFPatriarch patriarch = this.createDrawingPatriarch();
                    for (int i = patriarch.getChildren().size() - 1; i >= 0; --i) {
                        final HSSFShape shape = patriarch.getChildren().get(i);
                        if (shape instanceof HSSFComment) {
                            final HSSFComment comment = (HSSFComment)shape;
                            if (comment.getRow() == rowNum) {
                                comment.setRow(rowNum + n);
                            }
                        }
                    }
                }
            }
        }
        if (n > 0) {
            if (startRow == this._firstrow) {
                this._firstrow = Math.max(startRow + n, 0);
                for (int j = startRow + 1; j < startRow + n; ++j) {
                    if (this.getRow(j) != null) {
                        this._firstrow = j;
                        break;
                    }
                }
            }
            if (endRow + n > this._lastrow) {
                this._lastrow = Math.min(endRow + n, SpreadsheetVersion.EXCEL97.getLastRowIndex());
            }
        }
        else {
            if (startRow + n < this._firstrow) {
                this._firstrow = Math.max(startRow + n, 0);
            }
            if (endRow == this._lastrow) {
                this._lastrow = Math.min(endRow + n, SpreadsheetVersion.EXCEL97.getLastRowIndex());
                for (int j = endRow - 1; j > endRow + n; ++j) {
                    if (this.getRow(j) != null) {
                        this._lastrow = j;
                        break;
                    }
                }
            }
        }
        final int sheetIndex = this._workbook.getSheetIndex(this);
        final String sheetName = this._workbook.getSheetName(sheetIndex);
        final short externSheetIndex = this._book.checkExternSheet(sheetIndex);
        final FormulaShifter shifter = FormulaShifter.createForRowShift(externSheetIndex, sheetName, startRow, endRow, n);
        this._sheet.updateFormulasAfterCellShift(shifter, externSheetIndex);
        for (int nSheets = this._workbook.getNumberOfSheets(), k = 0; k < nSheets; ++k) {
            final InternalSheet otherSheet = this._workbook.getSheetAt(k).getSheet();
            if (otherSheet != this._sheet) {
                final short otherExtSheetIx = this._book.checkExternSheet(k);
                otherSheet.updateFormulasAfterCellShift(shifter, otherExtSheetIx);
            }
        }
        this._workbook.getWorkbook().updateNamesAfterCellShift(shifter);
    }
    
    protected void insertChartRecords(final List<Record> records) {
        final int window2Loc = this._sheet.findFirstRecordLocBySid((short)574);
        this._sheet.getRecords().addAll(window2Loc, records);
    }
    
    private void notifyRowShifting(final HSSFRow row) {
        final String msg = "Row[rownum=" + row.getRowNum() + "] contains cell(s) included in a multi-cell array formula. " + "You cannot change part of an array.";
        for (final Cell cell : row) {
            final HSSFCell hcell = (HSSFCell)cell;
            if (hcell.isPartOfArrayFormulaGroup()) {
                hcell.notifyArrayFormulaChanging(msg);
            }
        }
    }
    
    @Override
    public void createFreezePane(final int colSplit, final int rowSplit, final int leftmostColumn, final int topRow) {
        this.validateColumn(colSplit);
        this.validateRow(rowSplit);
        if (leftmostColumn < colSplit) {
            throw new IllegalArgumentException("leftmostColumn parameter must not be less than colSplit parameter");
        }
        if (topRow < rowSplit) {
            throw new IllegalArgumentException("topRow parameter must not be less than leftmostColumn parameter");
        }
        this.getSheet().createFreezePane(colSplit, rowSplit, topRow, leftmostColumn);
    }
    
    @Override
    public void createFreezePane(final int colSplit, final int rowSplit) {
        this.createFreezePane(colSplit, rowSplit, colSplit, rowSplit);
    }
    
    @Override
    public void createSplitPane(final int xSplitPos, final int ySplitPos, final int leftmostColumn, final int topRow, final int activePane) {
        this.getSheet().createSplitPane(xSplitPos, ySplitPos, topRow, leftmostColumn, activePane);
    }
    
    @Override
    public PaneInformation getPaneInformation() {
        return this.getSheet().getPaneInformation();
    }
    
    @Override
    public void setDisplayGridlines(final boolean show) {
        this._sheet.setDisplayGridlines(show);
    }
    
    @Override
    public boolean isDisplayGridlines() {
        return this._sheet.isDisplayGridlines();
    }
    
    @Override
    public void setDisplayFormulas(final boolean show) {
        this._sheet.setDisplayFormulas(show);
    }
    
    @Override
    public boolean isDisplayFormulas() {
        return this._sheet.isDisplayFormulas();
    }
    
    @Override
    public void setDisplayRowColHeadings(final boolean show) {
        this._sheet.setDisplayRowColHeadings(show);
    }
    
    @Override
    public boolean isDisplayRowColHeadings() {
        return this._sheet.isDisplayRowColHeadings();
    }
    
    @Override
    public void setRowBreak(final int row) {
        this.validateRow(row);
        this._sheet.getPageSettings().setRowBreak(row, (short)0, (short)255);
    }
    
    @Override
    public boolean isRowBroken(final int row) {
        return this._sheet.getPageSettings().isRowBroken(row);
    }
    
    @Override
    public void removeRowBreak(final int row) {
        this._sheet.getPageSettings().removeRowBreak(row);
    }
    
    @Override
    public int[] getRowBreaks() {
        return this._sheet.getPageSettings().getRowBreaks();
    }
    
    @Override
    public int[] getColumnBreaks() {
        return this._sheet.getPageSettings().getColumnBreaks();
    }
    
    @Override
    public void setColumnBreak(final int column) {
        this.validateColumn((short)column);
        this._sheet.getPageSettings().setColumnBreak((short)column, (short)0, (short)SpreadsheetVersion.EXCEL97.getLastRowIndex());
    }
    
    @Override
    public boolean isColumnBroken(final int column) {
        return this._sheet.getPageSettings().isColumnBroken(column);
    }
    
    @Override
    public void removeColumnBreak(final int column) {
        this._sheet.getPageSettings().removeColumnBreak(column);
    }
    
    protected void validateRow(final int row) {
        final int maxrow = SpreadsheetVersion.EXCEL97.getLastRowIndex();
        if (row > maxrow) {
            throw new IllegalArgumentException("Maximum row number is " + maxrow);
        }
        if (row < 0) {
            throw new IllegalArgumentException("Minumum row number is 0");
        }
    }
    
    protected void validateColumn(final int column) {
        final int maxcol = SpreadsheetVersion.EXCEL97.getLastColumnIndex();
        if (column > maxcol) {
            throw new IllegalArgumentException("Maximum column number is " + maxcol);
        }
        if (column < 0) {
            throw new IllegalArgumentException("Minimum column number is 0");
        }
    }
    
    public void dumpDrawingRecords(final boolean fat) {
        this._sheet.aggregateDrawingRecords(this._book.getDrawingManager(), false);
        final EscherAggregate r = (EscherAggregate)this.getSheet().findFirstRecordBySid((short)9876);
        final List<EscherRecord> escherRecords = r.getEscherRecords();
        final PrintWriter w = new PrintWriter(System.out);
        for (final EscherRecord escherRecord : escherRecords) {
            if (fat) {
                System.out.println(escherRecord.toString());
            }
            else {
                escherRecord.display(w, 0);
            }
        }
        w.flush();
    }
    
    public EscherAggregate getDrawingEscherAggregate() {
        this._book.findDrawingGroup();
        if (this._book.getDrawingManager() == null) {
            return null;
        }
        final int found = this._sheet.aggregateDrawingRecords(this._book.getDrawingManager(), false);
        if (found == -1) {
            return null;
        }
        final EscherAggregate agg = (EscherAggregate)this._sheet.findFirstRecordBySid((short)9876);
        return agg;
    }
    
    public HSSFPatriarch getDrawingPatriarch() {
        return this._patriarch = this.getPatriarch(false);
    }
    
    @Override
    public HSSFPatriarch createDrawingPatriarch() {
        return this._patriarch = this.getPatriarch(true);
    }
    
    private HSSFPatriarch getPatriarch(final boolean createIfMissing) {
        HSSFPatriarch patriarch = null;
        if (this._patriarch != null) {
            return this._patriarch;
        }
        DrawingManager2 dm = this._book.findDrawingGroup();
        if (null == dm) {
            if (!createIfMissing) {
                return null;
            }
            this._book.createDrawingGroup();
            dm = this._book.getDrawingManager();
        }
        EscherAggregate agg = (EscherAggregate)this._sheet.findFirstRecordBySid((short)9876);
        if (null == agg) {
            int pos = this._sheet.aggregateDrawingRecords(dm, false);
            if (-1 == pos) {
                if (createIfMissing) {
                    pos = this._sheet.aggregateDrawingRecords(dm, true);
                    agg = this._sheet.getRecords().get(pos);
                    patriarch = new HSSFPatriarch(this, agg);
                    patriarch.afterCreate();
                    return patriarch;
                }
                return null;
            }
            else {
                agg = this._sheet.getRecords().get(pos);
            }
        }
        return new HSSFPatriarch(this, agg);
    }
    
    @Deprecated
    public void setColumnGroupCollapsed(final short columnNumber, final boolean collapsed) {
        this.setColumnGroupCollapsed(columnNumber & 0xFFFF, collapsed);
    }
    
    @Deprecated
    public void groupColumn(final short fromColumn, final short toColumn) {
        this.groupColumn(fromColumn & 0xFFFF, toColumn & 0xFFFF);
    }
    
    @Deprecated
    public void ungroupColumn(final short fromColumn, final short toColumn) {
        this.ungroupColumn(fromColumn & 0xFFFF, toColumn & 0xFFFF);
    }
    
    @Override
    public void setColumnGroupCollapsed(final int columnNumber, final boolean collapsed) {
        this._sheet.setColumnGroupCollapsed(columnNumber, collapsed);
    }
    
    @Override
    public void groupColumn(final int fromColumn, final int toColumn) {
        this._sheet.groupColumnRange(fromColumn, toColumn, true);
    }
    
    @Override
    public void ungroupColumn(final int fromColumn, final int toColumn) {
        this._sheet.groupColumnRange(fromColumn, toColumn, false);
    }
    
    @Override
    public void groupRow(final int fromRow, final int toRow) {
        this._sheet.groupRowRange(fromRow, toRow, true);
    }
    
    @Override
    public void ungroupRow(final int fromRow, final int toRow) {
        this._sheet.groupRowRange(fromRow, toRow, false);
    }
    
    @Override
    public void setRowGroupCollapsed(final int rowIndex, final boolean collapse) {
        if (collapse) {
            this._sheet.getRowsAggregate().collapseRow(rowIndex);
        }
        else {
            this._sheet.getRowsAggregate().expandRow(rowIndex);
        }
    }
    
    @Override
    public void setDefaultColumnStyle(final int column, final CellStyle style) {
        this._sheet.setDefaultColumnStyle(column, ((HSSFCellStyle)style).getIndex());
    }
    
    @Override
    public void autoSizeColumn(final int column) {
        this.autoSizeColumn(column, false);
    }
    
    @Override
    public void autoSizeColumn(final int column, final boolean useMergedCells) {
        double width = SheetUtil.getColumnWidth(this, column, useMergedCells);
        if (width != -1.0) {
            width *= 256.0;
            final int maxColumnWidth = 65280;
            if (width > maxColumnWidth) {
                width = maxColumnWidth;
            }
            this.setColumnWidth(column, (int)width);
        }
    }
    
    @Override
    public HSSFComment getCellComment(final int row, final int column) {
        return this.findCellComment(row, column);
    }
    
    @Override
    public HSSFSheetConditionalFormatting getSheetConditionalFormatting() {
        return new HSSFSheetConditionalFormatting(this);
    }
    
    @Override
    public String getSheetName() {
        final HSSFWorkbook wb = this.getWorkbook();
        final int idx = wb.getSheetIndex(this);
        return wb.getSheetName(idx);
    }
    
    private CellRange<HSSFCell> getCellRange(final CellRangeAddress range) {
        final int firstRow = range.getFirstRow();
        final int firstColumn = range.getFirstColumn();
        final int lastRow = range.getLastRow();
        final int lastColumn = range.getLastColumn();
        final int height = lastRow - firstRow + 1;
        final int width = lastColumn - firstColumn + 1;
        final List<HSSFCell> temp = new ArrayList<HSSFCell>(height * width);
        for (int rowIn = firstRow; rowIn <= lastRow; ++rowIn) {
            for (int colIn = firstColumn; colIn <= lastColumn; ++colIn) {
                HSSFRow row = this.getRow(rowIn);
                if (row == null) {
                    row = this.createRow(rowIn);
                }
                HSSFCell cell = row.getCell(colIn);
                if (cell == null) {
                    cell = row.createCell(colIn);
                }
                temp.add(cell);
            }
        }
        return SSCellRange.create(firstRow, firstColumn, height, width, temp, HSSFCell.class);
    }
    
    @Override
    public CellRange<HSSFCell> setArrayFormula(final String formula, final CellRangeAddress range) {
        final int sheetIndex = this._workbook.getSheetIndex(this);
        final Ptg[] ptgs = HSSFFormulaParser.parse(formula, this._workbook, 2, sheetIndex);
        final CellRange<HSSFCell> cells = this.getCellRange(range);
        for (final HSSFCell c : cells) {
            c.setCellArrayFormula(range);
        }
        final HSSFCell mainArrayFormulaCell = cells.getTopLeftCell();
        final FormulaRecordAggregate agg = (FormulaRecordAggregate)mainArrayFormulaCell.getCellValueRecord();
        agg.setArrayFormula(range, ptgs);
        return cells;
    }
    
    @Override
    public CellRange<HSSFCell> removeArrayFormula(final Cell cell) {
        if (cell.getSheet() != this) {
            throw new IllegalArgumentException("Specified cell does not belong to this sheet.");
        }
        final CellValueRecordInterface rec = ((HSSFCell)cell).getCellValueRecord();
        if (!(rec instanceof FormulaRecordAggregate)) {
            final String ref = new CellReference(cell).formatAsString();
            throw new IllegalArgumentException("Cell " + ref + " is not part of an array formula.");
        }
        final FormulaRecordAggregate fra = (FormulaRecordAggregate)rec;
        final CellRangeAddress range = fra.removeArrayFormula(cell.getRowIndex(), cell.getColumnIndex());
        final CellRange<HSSFCell> result = this.getCellRange(range);
        for (final Cell c : result) {
            c.setCellType(3);
        }
        return result;
    }
    
    @Override
    public DataValidationHelper getDataValidationHelper() {
        return new HSSFDataValidationHelper(this);
    }
    
    @Override
    public HSSFAutoFilter setAutoFilter(final CellRangeAddress range) {
        final InternalWorkbook workbook = this._workbook.getWorkbook();
        final int sheetIndex = this._workbook.getSheetIndex(this);
        NameRecord name = workbook.getSpecificBuiltinRecord((byte)13, sheetIndex + 1);
        if (name == null) {
            name = workbook.createBuiltInName((byte)13, sheetIndex + 1);
        }
        int firstRow = range.getFirstRow();
        if (firstRow == -1) {
            firstRow = 0;
        }
        final Area3DPtg ptg = new Area3DPtg(firstRow, range.getLastRow(), range.getFirstColumn(), range.getLastColumn(), false, false, false, false, sheetIndex);
        name.setNameDefinition(new Ptg[] { ptg });
        final AutoFilterInfoRecord r = new AutoFilterInfoRecord();
        final int numcols = 1 + range.getLastColumn() - range.getFirstColumn();
        r.setNumEntries((short)numcols);
        final int idx = this._sheet.findFirstRecordLocBySid((short)512);
        this._sheet.getRecords().add(idx, r);
        final HSSFPatriarch p = this.createDrawingPatriarch();
        for (int col = range.getFirstColumn(); col <= range.getLastColumn(); ++col) {
            p.createComboBox(new HSSFClientAnchor(0, 0, 0, 0, (short)col, firstRow, (short)(col + 1), firstRow + 1));
        }
        return new HSSFAutoFilter(this);
    }
    
    protected HSSFComment findCellComment(final int row, final int column) {
        HSSFPatriarch patriarch = this.getDrawingPatriarch();
        if (null == patriarch) {
            patriarch = this.createDrawingPatriarch();
        }
        return this.lookForComment(patriarch, row, column);
    }
    
    private HSSFComment lookForComment(final HSSFShapeContainer container, final int row, final int column) {
        for (final Object object : container.getChildren()) {
            final HSSFShape shape = (HSSFShape)object;
            if (shape instanceof HSSFShapeGroup) {
                final HSSFShape res = this.lookForComment((HSSFShapeContainer)shape, row, column);
                if (null != res) {
                    return (HSSFComment)res;
                }
                continue;
            }
            else {
                if (!(shape instanceof HSSFComment)) {
                    continue;
                }
                final HSSFComment comment = (HSSFComment)shape;
                if (comment.hasPosition() && comment.getColumn() == column && comment.getRow() == row) {
                    return comment;
                }
                continue;
            }
        }
        return null;
    }
    
    @Override
    public CellRangeAddress getRepeatingRows() {
        return this.getRepeatingRowsOrColums(true);
    }
    
    @Override
    public CellRangeAddress getRepeatingColumns() {
        return this.getRepeatingRowsOrColums(false);
    }
    
    @Override
    public void setRepeatingRows(final CellRangeAddress rowRangeRef) {
        final CellRangeAddress columnRangeRef = this.getRepeatingColumns();
        this.setRepeatingRowsAndColumns(rowRangeRef, columnRangeRef);
    }
    
    @Override
    public void setRepeatingColumns(final CellRangeAddress columnRangeRef) {
        final CellRangeAddress rowRangeRef = this.getRepeatingRows();
        this.setRepeatingRowsAndColumns(rowRangeRef, columnRangeRef);
    }
    
    private void setRepeatingRowsAndColumns(final CellRangeAddress rowDef, final CellRangeAddress colDef) {
        final int sheetIndex = this._workbook.getSheetIndex(this);
        final int maxRowIndex = SpreadsheetVersion.EXCEL97.getLastRowIndex();
        final int maxColIndex = SpreadsheetVersion.EXCEL97.getLastColumnIndex();
        int col1 = -1;
        int col2 = -1;
        int row1 = -1;
        int row2 = -1;
        if (rowDef != null) {
            row1 = rowDef.getFirstRow();
            row2 = rowDef.getLastRow();
            if ((row1 == -1 && row2 != -1) || row1 > row2 || row1 < 0 || row1 > maxRowIndex || row2 < 0 || row2 > maxRowIndex) {
                throw new IllegalArgumentException("Invalid row range specification");
            }
        }
        if (colDef != null) {
            col1 = colDef.getFirstColumn();
            col2 = colDef.getLastColumn();
            if ((col1 == -1 && col2 != -1) || col1 > col2 || col1 < 0 || col1 > maxColIndex || col2 < 0 || col2 > maxColIndex) {
                throw new IllegalArgumentException("Invalid column range specification");
            }
        }
        final short externSheetIndex = this._workbook.getWorkbook().checkExternSheet(sheetIndex);
        final boolean setBoth = rowDef != null && colDef != null;
        final boolean removeAll = rowDef == null && colDef == null;
        HSSFName name = this._workbook.getBuiltInName((byte)7, sheetIndex);
        if (removeAll) {
            if (name != null) {
                this._workbook.removeName(name);
            }
            return;
        }
        if (name == null) {
            name = this._workbook.createBuiltInName((byte)7, sheetIndex);
        }
        final List<Ptg> ptgList = new ArrayList<Ptg>();
        if (setBoth) {
            final int exprsSize = 23;
            ptgList.add(new MemFuncPtg(23));
        }
        if (colDef != null) {
            final Area3DPtg colArea = new Area3DPtg(0, maxRowIndex, col1, col2, false, false, false, false, externSheetIndex);
            ptgList.add(colArea);
        }
        if (rowDef != null) {
            final Area3DPtg rowArea = new Area3DPtg(row1, row2, 0, maxColIndex, false, false, false, false, externSheetIndex);
            ptgList.add(rowArea);
        }
        if (setBoth) {
            ptgList.add(UnionPtg.instance);
        }
        final Ptg[] ptgs = new Ptg[ptgList.size()];
        ptgList.toArray(ptgs);
        name.setNameDefinition(ptgs);
        final HSSFPrintSetup printSetup = this.getPrintSetup();
        printSetup.setValidSettings(false);
        this.setActive(true);
    }
    
    private CellRangeAddress getRepeatingRowsOrColums(final boolean rows) {
        final NameRecord rec = this.getBuiltinNameRecord((byte)7);
        if (rec == null) {
            return null;
        }
        final Ptg[] nameDefinition = rec.getNameDefinition();
        if (nameDefinition == null) {
            return null;
        }
        final int maxRowIndex = SpreadsheetVersion.EXCEL97.getLastRowIndex();
        final int maxColIndex = SpreadsheetVersion.EXCEL97.getLastColumnIndex();
        for (final Ptg ptg : nameDefinition) {
            if (ptg instanceof Area3DPtg) {
                final Area3DPtg areaPtg = (Area3DPtg)ptg;
                if (areaPtg.getFirstColumn() == 0 && areaPtg.getLastColumn() == maxColIndex) {
                    if (rows) {
                        final CellRangeAddress rowRange = new CellRangeAddress(areaPtg.getFirstRow(), areaPtg.getLastRow(), -1, -1);
                        return rowRange;
                    }
                }
                else if (areaPtg.getFirstRow() == 0 && areaPtg.getLastRow() == maxRowIndex && !rows) {
                    final CellRangeAddress columnRange = new CellRangeAddress(-1, -1, areaPtg.getFirstColumn(), areaPtg.getLastColumn());
                    return columnRange;
                }
            }
        }
        return null;
    }
    
    private NameRecord getBuiltinNameRecord(final byte builtinCode) {
        final int sheetIndex = this._workbook.getSheetIndex(this);
        final int recIndex = this._workbook.findExistingBuiltinNameRecordIdx(sheetIndex, builtinCode);
        if (recIndex == -1) {
            return null;
        }
        return this._workbook.getNameRecord(recIndex);
    }
    
    @Override
    public int getColumnOutlineLevel(final int columnIndex) {
        return this._sheet.getColumnOutlineLevel(columnIndex);
    }
    
    static {
        log = POILogFactory.getLogger(HSSFSheet.class);
        INITIAL_CAPACITY = Configurator.getIntValue("HSSFSheet.RowInitialCapacity", 20);
    }
}
