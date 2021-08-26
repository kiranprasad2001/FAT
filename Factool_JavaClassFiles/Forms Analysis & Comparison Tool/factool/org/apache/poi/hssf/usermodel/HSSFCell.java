// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.formula.ptg.ExpPtg;
import org.apache.poi.ss.util.CellRangeAddress;
import java.util.List;
import org.apache.poi.ss.usermodel.Hyperlink;
import java.util.Iterator;
import org.apache.poi.hssf.record.HyperlinkRecord;
import org.apache.poi.hssf.record.RecordBase;
import org.apache.poi.ss.usermodel.Comment;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.hssf.record.ExtendedFormatRecord;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ErrorConstants;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.RichTextString;
import java.util.Calendar;
import org.apache.poi.ss.usermodel.DateUtil;
import java.util.Date;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.hssf.record.BlankRecord;
import org.apache.poi.hssf.record.common.UnicodeString;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.hssf.record.BoolErrRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.aggregates.FormulaRecordAggregate;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.CellValueRecordInterface;
import org.apache.poi.util.POILogger;
import org.apache.poi.ss.usermodel.Cell;

public class HSSFCell implements Cell
{
    private static POILogger log;
    private static final String FILE_FORMAT_NAME = "BIFF8";
    public static final int LAST_COLUMN_NUMBER;
    private static final String LAST_COLUMN_NAME;
    public static final short ENCODING_UNCHANGED = -1;
    public static final short ENCODING_COMPRESSED_UNICODE = 0;
    public static final short ENCODING_UTF_16 = 1;
    private final HSSFWorkbook _book;
    private final HSSFSheet _sheet;
    private int _cellType;
    private HSSFRichTextString _stringValue;
    private CellValueRecordInterface _record;
    private HSSFComment _comment;
    
    protected HSSFCell(final HSSFWorkbook book, final HSSFSheet sheet, final int row, final short col) {
        checkBounds(col);
        this._stringValue = null;
        this._book = book;
        this._sheet = sheet;
        final short xfindex = sheet.getSheet().getXFIndexForColAt(col);
        this.setCellType(3, false, row, col, xfindex);
    }
    
    @Override
    public HSSFSheet getSheet() {
        return this._sheet;
    }
    
    @Override
    public HSSFRow getRow() {
        final int rowIndex = this.getRowIndex();
        return this._sheet.getRow(rowIndex);
    }
    
    protected HSSFCell(final HSSFWorkbook book, final HSSFSheet sheet, final int row, final short col, final int type) {
        checkBounds(col);
        this._cellType = -1;
        this._stringValue = null;
        this._book = book;
        this._sheet = sheet;
        final short xfindex = sheet.getSheet().getXFIndexForColAt(col);
        this.setCellType(type, false, row, col, xfindex);
    }
    
    protected HSSFCell(final HSSFWorkbook book, final HSSFSheet sheet, final CellValueRecordInterface cval) {
        this._record = cval;
        this._cellType = determineType(cval);
        this._stringValue = null;
        this._book = book;
        this._sheet = sheet;
        switch (this._cellType) {
            case 1: {
                this._stringValue = new HSSFRichTextString(book.getWorkbook(), (LabelSSTRecord)cval);
            }
            case 2: {
                this._stringValue = new HSSFRichTextString(((FormulaRecordAggregate)cval).getStringValue());
                break;
            }
        }
    }
    
    private static int determineType(final CellValueRecordInterface cval) {
        if (cval instanceof FormulaRecordAggregate) {
            return 2;
        }
        final Record record = (Record)cval;
        switch (record.getSid()) {
            case 515: {
                return 0;
            }
            case 513: {
                return 3;
            }
            case 253: {
                return 1;
            }
            case 517: {
                final BoolErrRecord boolErrRecord = (BoolErrRecord)record;
                return boolErrRecord.isBoolean() ? 4 : 5;
            }
            default: {
                throw new RuntimeException("Bad cell value rec (" + cval.getClass().getName() + ")");
            }
        }
    }
    
    protected InternalWorkbook getBoundWorkbook() {
        return this._book.getWorkbook();
    }
    
    @Override
    public int getRowIndex() {
        return this._record.getRow();
    }
    
    @Deprecated
    public void setCellNum(final short num) {
        this._record.setColumn(num);
    }
    
    protected void updateCellNum(final short num) {
        this._record.setColumn(num);
    }
    
    @Deprecated
    public short getCellNum() {
        return (short)this.getColumnIndex();
    }
    
    @Override
    public int getColumnIndex() {
        return this._record.getColumn() & 0xFFFF;
    }
    
    @Override
    public void setCellType(final int cellType) {
        this.notifyFormulaChanging();
        if (this.isPartOfArrayFormulaGroup()) {
            this.notifyArrayFormulaChanging();
        }
        final int row = this._record.getRow();
        final short col = this._record.getColumn();
        final short styleIndex = this._record.getXFIndex();
        this.setCellType(cellType, true, row, col, styleIndex);
    }
    
    private void setCellType(final int cellType, final boolean setValue, final int row, final short col, final short styleIndex) {
        if (cellType > 5) {
            throw new RuntimeException("I have no idea what type that is!");
        }
        switch (cellType) {
            case 2: {
                FormulaRecordAggregate frec;
                if (cellType != this._cellType) {
                    frec = this._sheet.getSheet().getRowsAggregate().createFormula(row, col);
                }
                else {
                    frec = (FormulaRecordAggregate)this._record;
                    frec.setRow(row);
                    frec.setColumn(col);
                }
                if (setValue) {
                    frec.getFormulaRecord().setValue(this.getNumericCellValue());
                }
                frec.setXFIndex(styleIndex);
                this._record = frec;
                break;
            }
            case 0: {
                NumberRecord nrec = null;
                if (cellType != this._cellType) {
                    nrec = new NumberRecord();
                }
                else {
                    nrec = (NumberRecord)this._record;
                }
                nrec.setColumn(col);
                if (setValue) {
                    nrec.setValue(this.getNumericCellValue());
                }
                nrec.setXFIndex(styleIndex);
                nrec.setRow(row);
                this._record = nrec;
                break;
            }
            case 1: {
                LabelSSTRecord lrec;
                if (cellType == this._cellType) {
                    lrec = (LabelSSTRecord)this._record;
                }
                else {
                    lrec = new LabelSSTRecord();
                    lrec.setColumn(col);
                    lrec.setRow(row);
                    lrec.setXFIndex(styleIndex);
                }
                if (setValue) {
                    final String str = this.convertCellValueToString();
                    final int sstIndex = this._book.getWorkbook().addSSTString(new UnicodeString(str));
                    lrec.setSSTIndex(sstIndex);
                    final UnicodeString us = this._book.getWorkbook().getSSTString(sstIndex);
                    (this._stringValue = new HSSFRichTextString()).setUnicodeString(us);
                }
                this._record = lrec;
                break;
            }
            case 3: {
                BlankRecord brec = null;
                if (cellType != this._cellType) {
                    brec = new BlankRecord();
                }
                else {
                    brec = (BlankRecord)this._record;
                }
                brec.setColumn(col);
                brec.setXFIndex(styleIndex);
                brec.setRow(row);
                this._record = brec;
                break;
            }
            case 4: {
                BoolErrRecord boolRec = null;
                if (cellType != this._cellType) {
                    boolRec = new BoolErrRecord();
                }
                else {
                    boolRec = (BoolErrRecord)this._record;
                }
                boolRec.setColumn(col);
                if (setValue) {
                    boolRec.setValue(this.convertCellValueToBoolean());
                }
                boolRec.setXFIndex(styleIndex);
                boolRec.setRow(row);
                this._record = boolRec;
                break;
            }
            case 5: {
                BoolErrRecord errRec = null;
                if (cellType != this._cellType) {
                    errRec = new BoolErrRecord();
                }
                else {
                    errRec = (BoolErrRecord)this._record;
                }
                errRec.setColumn(col);
                if (setValue) {
                    errRec.setValue((byte)15);
                }
                errRec.setXFIndex(styleIndex);
                errRec.setRow(row);
                this._record = errRec;
                break;
            }
        }
        if (cellType != this._cellType && this._cellType != -1) {
            this._sheet.getSheet().replaceValueRecord(this._record);
        }
        this._cellType = cellType;
    }
    
    @Override
    public int getCellType() {
        return this._cellType;
    }
    
    @Override
    public void setCellValue(final double value) {
        if (Double.isInfinite(value)) {
            this.setCellErrorValue(FormulaError.DIV0.getCode());
        }
        else if (Double.isNaN(value)) {
            this.setCellErrorValue(FormulaError.NUM.getCode());
        }
        else {
            final int row = this._record.getRow();
            final short col = this._record.getColumn();
            final short styleIndex = this._record.getXFIndex();
            switch (this._cellType) {
                default: {
                    this.setCellType(0, false, row, col, styleIndex);
                }
                case 0: {
                    ((NumberRecord)this._record).setValue(value);
                    break;
                }
                case 2: {
                    ((FormulaRecordAggregate)this._record).setCachedDoubleResult(value);
                    break;
                }
            }
        }
    }
    
    @Override
    public void setCellValue(final Date value) {
        this.setCellValue(DateUtil.getExcelDate(value, this._book.getWorkbook().isUsing1904DateWindowing()));
    }
    
    @Override
    public void setCellValue(final Calendar value) {
        this.setCellValue(DateUtil.getExcelDate(value, this._book.getWorkbook().isUsing1904DateWindowing()));
    }
    
    @Override
    public void setCellValue(final String value) {
        final HSSFRichTextString str = (value == null) ? null : new HSSFRichTextString(value);
        this.setCellValue(str);
    }
    
    @Override
    public void setCellValue(final RichTextString value) {
        final int row = this._record.getRow();
        final short col = this._record.getColumn();
        final short styleIndex = this._record.getXFIndex();
        if (value == null) {
            this.notifyFormulaChanging();
            this.setCellType(3, false, row, col, styleIndex);
            return;
        }
        if (value.length() > SpreadsheetVersion.EXCEL97.getMaxTextLength()) {
            throw new IllegalArgumentException("The maximum length of cell contents (text) is 32,767 characters");
        }
        if (this._cellType == 2) {
            final FormulaRecordAggregate fr = (FormulaRecordAggregate)this._record;
            fr.setCachedStringResult(value.getString());
            this._stringValue = new HSSFRichTextString(value.getString());
            return;
        }
        if (this._cellType != 1) {
            this.setCellType(1, false, row, col, styleIndex);
        }
        int index = 0;
        final HSSFRichTextString hvalue = (HSSFRichTextString)value;
        final UnicodeString str = hvalue.getUnicodeString();
        index = this._book.getWorkbook().addSSTString(str);
        ((LabelSSTRecord)this._record).setSSTIndex(index);
        (this._stringValue = hvalue).setWorkbookReferences(this._book.getWorkbook(), (LabelSSTRecord)this._record);
        this._stringValue.setUnicodeString(this._book.getWorkbook().getSSTString(index));
    }
    
    @Override
    public void setCellFormula(final String formula) {
        if (this.isPartOfArrayFormulaGroup()) {
            this.notifyArrayFormulaChanging();
        }
        final int row = this._record.getRow();
        final short col = this._record.getColumn();
        final short styleIndex = this._record.getXFIndex();
        if (formula == null) {
            this.notifyFormulaChanging();
            this.setCellType(3, false, row, col, styleIndex);
            return;
        }
        final int sheetIndex = this._book.getSheetIndex(this._sheet);
        final Ptg[] ptgs = HSSFFormulaParser.parse(formula, this._book, 0, sheetIndex);
        this.setCellType(2, false, row, col, styleIndex);
        final FormulaRecordAggregate agg = (FormulaRecordAggregate)this._record;
        final FormulaRecord frec = agg.getFormulaRecord();
        frec.setOptions((short)2);
        frec.setValue(0.0);
        if (agg.getXFIndex() == 0) {
            agg.setXFIndex((short)15);
        }
        agg.setParsedExpression(ptgs);
    }
    
    private void notifyFormulaChanging() {
        if (this._record instanceof FormulaRecordAggregate) {
            ((FormulaRecordAggregate)this._record).notifyFormulaChanging();
        }
    }
    
    @Override
    public String getCellFormula() {
        if (!(this._record instanceof FormulaRecordAggregate)) {
            throw typeMismatch(2, this._cellType, true);
        }
        return HSSFFormulaParser.toFormulaString(this._book, ((FormulaRecordAggregate)this._record).getFormulaTokens());
    }
    
    private static String getCellTypeName(final int cellTypeCode) {
        switch (cellTypeCode) {
            case 3: {
                return "blank";
            }
            case 1: {
                return "text";
            }
            case 4: {
                return "boolean";
            }
            case 5: {
                return "error";
            }
            case 0: {
                return "numeric";
            }
            case 2: {
                return "formula";
            }
            default: {
                return "#unknown cell type (" + cellTypeCode + ")#";
            }
        }
    }
    
    private static RuntimeException typeMismatch(final int expectedTypeCode, final int actualTypeCode, final boolean isFormulaCell) {
        final String msg = "Cannot get a " + getCellTypeName(expectedTypeCode) + " value from a " + getCellTypeName(actualTypeCode) + " " + (isFormulaCell ? "formula " : "") + "cell";
        return new IllegalStateException(msg);
    }
    
    private static void checkFormulaCachedValueType(final int expectedTypeCode, final FormulaRecord fr) {
        final int cachedValueType = fr.getCachedResultType();
        if (cachedValueType != expectedTypeCode) {
            throw typeMismatch(expectedTypeCode, cachedValueType, true);
        }
    }
    
    @Override
    public double getNumericCellValue() {
        switch (this._cellType) {
            case 3: {
                return 0.0;
            }
            case 0: {
                return ((NumberRecord)this._record).getValue();
            }
            default: {
                throw typeMismatch(0, this._cellType, false);
            }
            case 2: {
                final FormulaRecord fr = ((FormulaRecordAggregate)this._record).getFormulaRecord();
                checkFormulaCachedValueType(0, fr);
                return fr.getValue();
            }
        }
    }
    
    @Override
    public Date getDateCellValue() {
        if (this._cellType == 3) {
            return null;
        }
        final double value = this.getNumericCellValue();
        if (this._book.getWorkbook().isUsing1904DateWindowing()) {
            return DateUtil.getJavaDate(value, true);
        }
        return DateUtil.getJavaDate(value, false);
    }
    
    @Override
    public String getStringCellValue() {
        final HSSFRichTextString str = this.getRichStringCellValue();
        return str.getString();
    }
    
    @Override
    public HSSFRichTextString getRichStringCellValue() {
        switch (this._cellType) {
            case 3: {
                return new HSSFRichTextString("");
            }
            case 1: {
                return this._stringValue;
            }
            default: {
                throw typeMismatch(1, this._cellType, false);
            }
            case 2: {
                final FormulaRecordAggregate fra = (FormulaRecordAggregate)this._record;
                checkFormulaCachedValueType(1, fra.getFormulaRecord());
                final String strVal = fra.getStringValue();
                return new HSSFRichTextString((strVal == null) ? "" : strVal);
            }
        }
    }
    
    @Override
    public void setCellValue(final boolean value) {
        final int row = this._record.getRow();
        final short col = this._record.getColumn();
        final short styleIndex = this._record.getXFIndex();
        switch (this._cellType) {
            default: {
                this.setCellType(4, false, row, col, styleIndex);
            }
            case 4: {
                ((BoolErrRecord)this._record).setValue(value);
                break;
            }
            case 2: {
                ((FormulaRecordAggregate)this._record).setCachedBooleanResult(value);
                break;
            }
        }
    }
    
    @Override
    public void setCellErrorValue(final byte errorCode) {
        final int row = this._record.getRow();
        final short col = this._record.getColumn();
        final short styleIndex = this._record.getXFIndex();
        switch (this._cellType) {
            default: {
                this.setCellType(5, false, row, col, styleIndex);
            }
            case 5: {
                ((BoolErrRecord)this._record).setValue(errorCode);
                break;
            }
            case 2: {
                ((FormulaRecordAggregate)this._record).setCachedErrorResult(errorCode);
                break;
            }
        }
    }
    
    private boolean convertCellValueToBoolean() {
        switch (this._cellType) {
            case 4: {
                return ((BoolErrRecord)this._record).getBooleanValue();
            }
            case 1: {
                final int sstIndex = ((LabelSSTRecord)this._record).getSSTIndex();
                final String text = this._book.getWorkbook().getSSTString(sstIndex).getString();
                return Boolean.valueOf(text);
            }
            case 0: {
                return ((NumberRecord)this._record).getValue() != 0.0;
            }
            case 2: {
                final FormulaRecord fr = ((FormulaRecordAggregate)this._record).getFormulaRecord();
                checkFormulaCachedValueType(4, fr);
                return fr.getCachedBooleanValue();
            }
            case 3:
            case 5: {
                return false;
            }
            default: {
                throw new RuntimeException("Unexpected cell type (" + this._cellType + ")");
            }
        }
    }
    
    private String convertCellValueToString() {
        switch (this._cellType) {
            case 3: {
                return "";
            }
            case 4: {
                return ((BoolErrRecord)this._record).getBooleanValue() ? "TRUE" : "FALSE";
            }
            case 1: {
                final int sstIndex = ((LabelSSTRecord)this._record).getSSTIndex();
                return this._book.getWorkbook().getSSTString(sstIndex).getString();
            }
            case 0: {
                return NumberToTextConverter.toText(((NumberRecord)this._record).getValue());
            }
            case 5: {
                return ErrorConstants.getText(((BoolErrRecord)this._record).getErrorValue());
            }
            case 2: {
                final FormulaRecordAggregate fra = (FormulaRecordAggregate)this._record;
                final FormulaRecord fr = fra.getFormulaRecord();
                switch (fr.getCachedResultType()) {
                    case 4: {
                        return fr.getCachedBooleanValue() ? "TRUE" : "FALSE";
                    }
                    case 1: {
                        return fra.getStringValue();
                    }
                    case 0: {
                        return NumberToTextConverter.toText(fr.getValue());
                    }
                    case 5: {
                        return ErrorConstants.getText(fr.getCachedErrorValue());
                    }
                    default: {
                        throw new IllegalStateException("Unexpected formula result type (" + this._cellType + ")");
                    }
                }
                break;
            }
            default: {
                throw new IllegalStateException("Unexpected cell type (" + this._cellType + ")");
            }
        }
    }
    
    @Override
    public boolean getBooleanCellValue() {
        switch (this._cellType) {
            case 3: {
                return false;
            }
            case 4: {
                return ((BoolErrRecord)this._record).getBooleanValue();
            }
            default: {
                throw typeMismatch(4, this._cellType, false);
            }
            case 2: {
                final FormulaRecord fr = ((FormulaRecordAggregate)this._record).getFormulaRecord();
                checkFormulaCachedValueType(4, fr);
                return fr.getCachedBooleanValue();
            }
        }
    }
    
    @Override
    public byte getErrorCellValue() {
        switch (this._cellType) {
            case 5: {
                return ((BoolErrRecord)this._record).getErrorValue();
            }
            default: {
                throw typeMismatch(5, this._cellType, false);
            }
            case 2: {
                final FormulaRecord fr = ((FormulaRecordAggregate)this._record).getFormulaRecord();
                checkFormulaCachedValueType(5, fr);
                return (byte)fr.getCachedErrorValue();
            }
        }
    }
    
    @Override
    public void setCellStyle(final CellStyle style) {
        this.setCellStyle((HSSFCellStyle)style);
    }
    
    public void setCellStyle(final HSSFCellStyle style) {
        if (style == null) {
            this._record.setXFIndex((short)15);
            return;
        }
        style.verifyBelongsToWorkbook(this._book);
        short styleIndex;
        if (style.getUserStyleName() != null) {
            styleIndex = this.applyUserCellStyle(style);
        }
        else {
            styleIndex = style.getIndex();
        }
        this._record.setXFIndex(styleIndex);
    }
    
    @Override
    public HSSFCellStyle getCellStyle() {
        final short styleIndex = this._record.getXFIndex();
        final ExtendedFormatRecord xf = this._book.getWorkbook().getExFormatAt(styleIndex);
        return new HSSFCellStyle(styleIndex, xf, this._book);
    }
    
    protected CellValueRecordInterface getCellValueRecord() {
        return this._record;
    }
    
    private static void checkBounds(final int cellIndex) {
        if (cellIndex < 0 || cellIndex > HSSFCell.LAST_COLUMN_NUMBER) {
            throw new IllegalArgumentException("Invalid column index (" + cellIndex + ").  Allowable column range for " + "BIFF8" + " is (0.." + HSSFCell.LAST_COLUMN_NUMBER + ") or ('A'..'" + HSSFCell.LAST_COLUMN_NAME + "')");
        }
    }
    
    @Override
    public void setAsActiveCell() {
        final int row = this._record.getRow();
        final short col = this._record.getColumn();
        this._sheet.getSheet().setActiveCellRow(row);
        this._sheet.getSheet().setActiveCellCol(col);
    }
    
    @Override
    public String toString() {
        switch (this.getCellType()) {
            case 3: {
                return "";
            }
            case 4: {
                return this.getBooleanCellValue() ? "TRUE" : "FALSE";
            }
            case 5: {
                return ErrorEval.getText(((BoolErrRecord)this._record).getErrorValue());
            }
            case 2: {
                return this.getCellFormula();
            }
            case 0: {
                if (DateUtil.isCellDateFormatted(this)) {
                    final DateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                    return sdf.format(this.getDateCellValue());
                }
                return String.valueOf(this.getNumericCellValue());
            }
            case 1: {
                return this.getStringCellValue();
            }
            default: {
                return "Unknown Cell Type: " + this.getCellType();
            }
        }
    }
    
    @Override
    public void setCellComment(final Comment comment) {
        if (comment == null) {
            this.removeCellComment();
            return;
        }
        comment.setRow(this._record.getRow());
        comment.setColumn(this._record.getColumn());
        this._comment = (HSSFComment)comment;
    }
    
    @Override
    public HSSFComment getCellComment() {
        if (this._comment == null) {
            this._comment = this._sheet.findCellComment(this._record.getRow(), this._record.getColumn());
        }
        return this._comment;
    }
    
    @Override
    public void removeCellComment() {
        final HSSFComment comment = this._sheet.findCellComment(this._record.getRow(), this._record.getColumn());
        this._comment = null;
        if (null == comment) {
            return;
        }
        this._sheet.getDrawingPatriarch().removeShape(comment);
    }
    
    @Override
    public HSSFHyperlink getHyperlink() {
        for (final RecordBase rec : this._sheet.getSheet().getRecords()) {
            if (rec instanceof HyperlinkRecord) {
                final HyperlinkRecord link = (HyperlinkRecord)rec;
                if (link.getFirstColumn() == this._record.getColumn() && link.getFirstRow() == this._record.getRow()) {
                    return new HSSFHyperlink(link);
                }
                continue;
            }
        }
        return null;
    }
    
    @Override
    public void setHyperlink(final Hyperlink hyperlink) {
        if (hyperlink == null) {
            this.removeHyperlink();
            return;
        }
        final HSSFHyperlink link = (HSSFHyperlink)hyperlink;
        link.setFirstRow(this._record.getRow());
        link.setLastRow(this._record.getRow());
        link.setFirstColumn(this._record.getColumn());
        link.setLastColumn(this._record.getColumn());
        switch (link.getType()) {
            case 1:
            case 3: {
                link.setLabel("url");
                break;
            }
            case 4: {
                link.setLabel("file");
                break;
            }
            case 2: {
                link.setLabel("place");
                break;
            }
        }
        final List<RecordBase> records = this._sheet.getSheet().getRecords();
        final int eofLoc = records.size() - 1;
        records.add(eofLoc, link.record);
    }
    
    @Override
    public void removeHyperlink() {
        final Iterator<RecordBase> it = this._sheet.getSheet().getRecords().iterator();
        while (it.hasNext()) {
            final RecordBase rec = it.next();
            if (rec instanceof HyperlinkRecord) {
                final HyperlinkRecord link = (HyperlinkRecord)rec;
                if (link.getFirstColumn() == this._record.getColumn() && link.getFirstRow() == this._record.getRow()) {
                    it.remove();
                    return;
                }
                continue;
            }
        }
    }
    
    @Override
    public int getCachedFormulaResultType() {
        if (this._cellType != 2) {
            throw new IllegalStateException("Only formula cells have cached results");
        }
        return ((FormulaRecordAggregate)this._record).getFormulaRecord().getCachedResultType();
    }
    
    void setCellArrayFormula(final CellRangeAddress range) {
        final int row = this._record.getRow();
        final short col = this._record.getColumn();
        final short styleIndex = this._record.getXFIndex();
        this.setCellType(2, false, row, col, styleIndex);
        final Ptg[] ptgsForCell = { new ExpPtg(range.getFirstRow(), range.getFirstColumn()) };
        final FormulaRecordAggregate agg = (FormulaRecordAggregate)this._record;
        agg.setParsedExpression(ptgsForCell);
    }
    
    @Override
    public CellRangeAddress getArrayFormulaRange() {
        if (this._cellType != 2) {
            final String ref = new CellReference(this).formatAsString();
            throw new IllegalStateException("Cell " + ref + " is not part of an array formula.");
        }
        return ((FormulaRecordAggregate)this._record).getArrayFormulaRange();
    }
    
    @Override
    public boolean isPartOfArrayFormulaGroup() {
        return this._cellType == 2 && ((FormulaRecordAggregate)this._record).isPartOfArrayFormula();
    }
    
    void notifyArrayFormulaChanging(final String msg) {
        final CellRangeAddress cra = this.getArrayFormulaRange();
        if (cra.getNumberOfCells() > 1) {
            throw new IllegalStateException(msg);
        }
        this.getRow().getSheet().removeArrayFormula(this);
    }
    
    void notifyArrayFormulaChanging() {
        final CellReference ref = new CellReference(this);
        final String msg = "Cell " + ref.formatAsString() + " is part of a multi-cell array formula. " + "You cannot change part of an array.";
        this.notifyArrayFormulaChanging(msg);
    }
    
    private short applyUserCellStyle(final HSSFCellStyle style) {
        if (style.getUserStyleName() == null) {
            throw new IllegalArgumentException("Expected user-defined style");
        }
        final InternalWorkbook iwb = this._book.getWorkbook();
        short userXf = -1;
        final int numfmt = iwb.getNumExFormats();
        for (short i = 0; i < numfmt; ++i) {
            final ExtendedFormatRecord xf = iwb.getExFormatAt(i);
            if (xf.getXFType() == 0 && xf.getParentIndex() == style.getIndex()) {
                userXf = i;
                break;
            }
        }
        short styleIndex;
        if (userXf == -1) {
            final ExtendedFormatRecord xfr = iwb.createCellXF();
            xfr.cloneStyleFrom(iwb.getExFormatAt(style.getIndex()));
            xfr.setIndentionOptions((short)0);
            xfr.setXFType((short)0);
            xfr.setParentIndex(style.getIndex());
            styleIndex = (short)numfmt;
        }
        else {
            styleIndex = userXf;
        }
        return styleIndex;
    }
    
    static {
        HSSFCell.log = POILogFactory.getLogger(HSSFCell.class);
        LAST_COLUMN_NUMBER = SpreadsheetVersion.EXCEL97.getLastColumnIndex();
        LAST_COLUMN_NAME = SpreadsheetVersion.EXCEL97.getLastColumnName();
    }
}
