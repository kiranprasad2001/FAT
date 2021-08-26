// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.hssf.record.NameCommentRecord;
import org.apache.poi.hssf.record.NameRecord;
import org.apache.poi.ss.usermodel.Name;

public final class HSSFName implements Name
{
    private HSSFWorkbook _book;
    private NameRecord _definedNameRec;
    private NameCommentRecord _commentRec;
    
    HSSFName(final HSSFWorkbook book, final NameRecord name) {
        this(book, name, null);
    }
    
    HSSFName(final HSSFWorkbook book, final NameRecord name, final NameCommentRecord comment) {
        this._book = book;
        this._definedNameRec = name;
        this._commentRec = comment;
    }
    
    @Override
    public String getSheetName() {
        final int indexToExternSheet = this._definedNameRec.getExternSheetNumber();
        return this._book.getWorkbook().findSheetFirstNameFromExternSheet(indexToExternSheet);
    }
    
    @Override
    public String getNameName() {
        return this._definedNameRec.getNameText();
    }
    
    @Override
    public void setNameName(final String nameName) {
        validateName(nameName);
        final InternalWorkbook wb = this._book.getWorkbook();
        this._definedNameRec.setNameText(nameName);
        final int sheetNumber = this._definedNameRec.getSheetNumber();
        for (int i = wb.getNumNames() - 1; i >= 0; --i) {
            final NameRecord rec = wb.getNameRecord(i);
            if (rec != this._definedNameRec && rec.getNameText().equalsIgnoreCase(nameName) && sheetNumber == rec.getSheetNumber()) {
                final String msg = "The " + ((sheetNumber == 0) ? "workbook" : "sheet") + " already contains this name: " + nameName;
                this._definedNameRec.setNameText(nameName + "(2)");
                throw new IllegalArgumentException(msg);
            }
        }
        if (this._commentRec != null) {
            final String oldName = this._commentRec.getNameText();
            this._commentRec.setNameText(nameName);
            this._book.getWorkbook().updateNameCommentRecordCache(this._commentRec);
        }
    }
    
    private static void validateName(final String name) {
        if (name.length() == 0) {
            throw new IllegalArgumentException("Name cannot be blank");
        }
        final char c = name.charAt(0);
        if ((c != '_' && !Character.isLetter(c)) || name.indexOf(32) != -1) {
            throw new IllegalArgumentException("Invalid name: '" + name + "'; Names must begin with a letter or underscore and not contain spaces");
        }
    }
    
    @Deprecated
    public String getReference() {
        return this.getRefersToFormula();
    }
    
    @Deprecated
    public void setReference(final String ref) {
        this.setRefersToFormula(ref);
    }
    
    @Override
    public void setRefersToFormula(final String formulaText) {
        final Ptg[] ptgs = HSSFFormulaParser.parse(formulaText, this._book, 4, this.getSheetIndex());
        this._definedNameRec.setNameDefinition(ptgs);
    }
    
    @Override
    public String getRefersToFormula() {
        if (this._definedNameRec.isFunctionName()) {
            throw new IllegalStateException("Only applicable to named ranges");
        }
        final Ptg[] ptgs = this._definedNameRec.getNameDefinition();
        if (ptgs.length < 1) {
            return null;
        }
        return HSSFFormulaParser.toFormulaString(this._book, ptgs);
    }
    
    void setNameDefinition(final Ptg[] ptgs) {
        this._definedNameRec.setNameDefinition(ptgs);
    }
    
    @Override
    public boolean isDeleted() {
        final Ptg[] ptgs = this._definedNameRec.getNameDefinition();
        return Ptg.doesFormulaReferToDeletedCell(ptgs);
    }
    
    @Override
    public boolean isFunctionName() {
        return this._definedNameRec.isFunctionName();
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(64);
        sb.append(this.getClass().getName()).append(" [");
        sb.append(this._definedNameRec.getNameText());
        sb.append("]");
        return sb.toString();
    }
    
    @Override
    public void setSheetIndex(final int index) {
        final int lastSheetIx = this._book.getNumberOfSheets() - 1;
        if (index < -1 || index > lastSheetIx) {
            throw new IllegalArgumentException("Sheet index (" + index + ") is out of range" + ((lastSheetIx == -1) ? "" : (" (0.." + lastSheetIx + ")")));
        }
        this._definedNameRec.setSheetNumber(index + 1);
    }
    
    @Override
    public int getSheetIndex() {
        return this._definedNameRec.getSheetNumber() - 1;
    }
    
    @Override
    public String getComment() {
        if (this._commentRec != null && this._commentRec.getCommentText() != null && this._commentRec.getCommentText().length() > 0) {
            return this._commentRec.getCommentText();
        }
        return this._definedNameRec.getDescriptionText();
    }
    
    @Override
    public void setComment(final String comment) {
        this._definedNameRec.setDescriptionText(comment);
        if (this._commentRec != null) {
            this._commentRec.setCommentText(comment);
        }
    }
    
    @Override
    public void setFunction(final boolean value) {
        this._definedNameRec.setFunction(value);
    }
}
