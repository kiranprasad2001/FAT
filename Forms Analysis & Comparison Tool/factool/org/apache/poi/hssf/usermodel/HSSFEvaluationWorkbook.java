// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.SheetRangeIdentifier;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.apache.poi.hssf.record.aggregates.FormulaRecordAggregate;
import org.apache.poi.ss.formula.EvaluationCell;
import org.apache.poi.ss.formula.ptg.NamePtg;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.formula.EvaluationSheet;
import org.apache.poi.hssf.record.NameRecord;
import org.apache.poi.ss.formula.EvaluationName;
import org.apache.poi.ss.formula.ptg.NameXPtg;
import org.apache.poi.ss.formula.ptg.Area3DPtg;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.formula.ptg.Ref3DPtg;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.SheetIdentifier;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.util.POILogger;
import org.apache.poi.ss.formula.FormulaParsingWorkbook;
import org.apache.poi.ss.formula.EvaluationWorkbook;
import org.apache.poi.ss.formula.FormulaRenderingWorkbook;

public final class HSSFEvaluationWorkbook implements FormulaRenderingWorkbook, EvaluationWorkbook, FormulaParsingWorkbook
{
    private static POILogger logger;
    private final HSSFWorkbook _uBook;
    private final InternalWorkbook _iBook;
    
    public static HSSFEvaluationWorkbook create(final HSSFWorkbook book) {
        if (book == null) {
            return null;
        }
        return new HSSFEvaluationWorkbook(book);
    }
    
    private HSSFEvaluationWorkbook(final HSSFWorkbook book) {
        this._uBook = book;
        this._iBook = book.getWorkbook();
    }
    
    @Override
    public int getExternalSheetIndex(final String sheetName) {
        final int sheetIndex = this._uBook.getSheetIndex(sheetName);
        return this._iBook.checkExternSheet(sheetIndex);
    }
    
    @Override
    public int getExternalSheetIndex(final String workbookName, final String sheetName) {
        return this._iBook.getExternalSheetIndex(workbookName, sheetName);
    }
    
    @Override
    public Ptg get3DReferencePtg(final CellReference cr, final SheetIdentifier sheet) {
        final int extIx = this.getSheetExtIx(sheet);
        return new Ref3DPtg(cr, extIx);
    }
    
    @Override
    public Ptg get3DReferencePtg(final AreaReference areaRef, final SheetIdentifier sheet) {
        final int extIx = this.getSheetExtIx(sheet);
        return new Area3DPtg(areaRef, extIx);
    }
    
    @Override
    public NameXPtg getNameXPtg(final String name, final SheetIdentifier sheet) {
        final int sheetRefIndex = this.getSheetExtIx(sheet);
        return this._iBook.getNameXPtg(name, sheetRefIndex, this._uBook.getUDFFinder());
    }
    
    @Override
    public EvaluationName getName(final String name, final int sheetIndex) {
        for (int i = 0; i < this._iBook.getNumNames(); ++i) {
            final NameRecord nr = this._iBook.getNameRecord(i);
            if (nr.getSheetNumber() == sheetIndex + 1 && name.equalsIgnoreCase(nr.getNameText())) {
                return new Name(nr, i);
            }
        }
        return (sheetIndex == -1) ? null : this.getName(name, -1);
    }
    
    @Override
    public int getSheetIndex(final EvaluationSheet evalSheet) {
        final HSSFSheet sheet = ((HSSFEvaluationSheet)evalSheet).getHSSFSheet();
        return this._uBook.getSheetIndex(sheet);
    }
    
    @Override
    public int getSheetIndex(final String sheetName) {
        return this._uBook.getSheetIndex(sheetName);
    }
    
    @Override
    public String getSheetName(final int sheetIndex) {
        return this._uBook.getSheetName(sheetIndex);
    }
    
    @Override
    public EvaluationSheet getSheet(final int sheetIndex) {
        return new HSSFEvaluationSheet(this._uBook.getSheetAt(sheetIndex));
    }
    
    @Override
    public int convertFromExternSheetIndex(final int externSheetIndex) {
        return this._iBook.getFirstSheetIndexFromExternSheetIndex(externSheetIndex);
    }
    
    @Override
    public ExternalSheet getExternalSheet(final int externSheetIndex) {
        ExternalSheet sheet = this._iBook.getExternalSheet(externSheetIndex);
        if (sheet == null) {
            final int localSheetIndex = this.convertFromExternSheetIndex(externSheetIndex);
            if (localSheetIndex == -1) {
                return null;
            }
            if (localSheetIndex == -2) {
                return null;
            }
            final String sheetName = this.getSheetName(localSheetIndex);
            final int lastLocalSheetIndex = this._iBook.getLastSheetIndexFromExternSheetIndex(externSheetIndex);
            if (lastLocalSheetIndex == localSheetIndex) {
                sheet = new ExternalSheet(null, sheetName);
            }
            else {
                final String lastSheetName = this.getSheetName(lastLocalSheetIndex);
                sheet = new ExternalSheetRange(null, sheetName, lastSheetName);
            }
        }
        return sheet;
    }
    
    @Override
    public ExternalSheet getExternalSheet(final String firstSheetName, final String lastSheetName, final int externalWorkbookNumber) {
        throw new IllegalStateException("XSSF-style external references are not supported for HSSF");
    }
    
    @Override
    public ExternalName getExternalName(final int externSheetIndex, final int externNameIndex) {
        return this._iBook.getExternalName(externSheetIndex, externNameIndex);
    }
    
    @Override
    public ExternalName getExternalName(final String nameName, final String sheetName, final int externalWorkbookNumber) {
        throw new IllegalStateException("XSSF-style external names are not supported for HSSF");
    }
    
    @Override
    public String resolveNameXText(final NameXPtg n) {
        return this._iBook.resolveNameXText(n.getSheetRefIndex(), n.getNameIndex());
    }
    
    @Override
    public String getSheetFirstNameByExternSheet(final int externSheetIndex) {
        return this._iBook.findSheetFirstNameFromExternSheet(externSheetIndex);
    }
    
    @Override
    public String getSheetLastNameByExternSheet(final int externSheetIndex) {
        return this._iBook.findSheetLastNameFromExternSheet(externSheetIndex);
    }
    
    @Override
    public String getNameText(final NamePtg namePtg) {
        return this._iBook.getNameRecord(namePtg.getIndex()).getNameText();
    }
    
    @Override
    public EvaluationName getName(final NamePtg namePtg) {
        final int ix = namePtg.getIndex();
        return new Name(this._iBook.getNameRecord(ix), ix);
    }
    
    @Override
    public Ptg[] getFormulaTokens(final EvaluationCell evalCell) {
        final HSSFCell cell = ((HSSFEvaluationCell)evalCell).getHSSFCell();
        final FormulaRecordAggregate fra = (FormulaRecordAggregate)cell.getCellValueRecord();
        return fra.getFormulaTokens();
    }
    
    @Override
    public UDFFinder getUDFFinder() {
        return this._uBook.getUDFFinder();
    }
    
    private int getSheetExtIx(final SheetIdentifier sheetIden) {
        int extIx;
        if (sheetIden == null) {
            extIx = -1;
        }
        else {
            final String workbookName = sheetIden.getBookName();
            String lastSheetName;
            final String firstSheetName = lastSheetName = sheetIden.getSheetIdentifier().getName();
            if (sheetIden instanceof SheetRangeIdentifier) {
                lastSheetName = ((SheetRangeIdentifier)sheetIden).getLastSheetIdentifier().getName();
            }
            if (workbookName == null) {
                final int firstSheetIndex = this._uBook.getSheetIndex(firstSheetName);
                final int lastSheetIndex = this._uBook.getSheetIndex(lastSheetName);
                extIx = this._iBook.checkExternSheet(firstSheetIndex, lastSheetIndex);
            }
            else {
                extIx = this._iBook.getExternalSheetIndex(workbookName, firstSheetName, lastSheetName);
            }
        }
        return extIx;
    }
    
    @Override
    public SpreadsheetVersion getSpreadsheetVersion() {
        return SpreadsheetVersion.EXCEL97;
    }
    
    static {
        HSSFEvaluationWorkbook.logger = POILogFactory.getLogger(HSSFEvaluationWorkbook.class);
    }
    
    private static final class Name implements EvaluationName
    {
        private final NameRecord _nameRecord;
        private final int _index;
        
        public Name(final NameRecord nameRecord, final int index) {
            this._nameRecord = nameRecord;
            this._index = index;
        }
        
        @Override
        public Ptg[] getNameDefinition() {
            return this._nameRecord.getNameDefinition();
        }
        
        @Override
        public String getNameText() {
            return this._nameRecord.getNameText();
        }
        
        @Override
        public boolean hasFormula() {
            return this._nameRecord.hasFormula();
        }
        
        @Override
        public boolean isFunctionName() {
            return this._nameRecord.isFunctionName();
        }
        
        @Override
        public boolean isRange() {
            return this._nameRecord.hasFormula();
        }
        
        @Override
        public NamePtg createPtg() {
            return new NamePtg(this._index);
        }
    }
}
