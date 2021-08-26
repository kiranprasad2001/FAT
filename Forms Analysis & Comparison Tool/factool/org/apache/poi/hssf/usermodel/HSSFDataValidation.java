// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.record.DVRecord;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.usermodel.DataValidation;

public final class HSSFDataValidation implements DataValidation
{
    private String _prompt_title;
    private String _prompt_text;
    private String _error_title;
    private String _error_text;
    private int _errorStyle;
    private boolean _emptyCellAllowed;
    private boolean _suppress_dropdown_arrow;
    private boolean _showPromptBox;
    private boolean _showErrorBox;
    private CellRangeAddressList _regions;
    private DVConstraint _constraint;
    
    public HSSFDataValidation(final CellRangeAddressList regions, final DataValidationConstraint constraint) {
        this._errorStyle = 0;
        this._emptyCellAllowed = true;
        this._suppress_dropdown_arrow = false;
        this._showPromptBox = true;
        this._showErrorBox = true;
        this._regions = regions;
        this._constraint = (DVConstraint)constraint;
    }
    
    @Override
    public DataValidationConstraint getValidationConstraint() {
        return this._constraint;
    }
    
    public DVConstraint getConstraint() {
        return this._constraint;
    }
    
    @Override
    public CellRangeAddressList getRegions() {
        return this._regions;
    }
    
    @Override
    public void setErrorStyle(final int error_style) {
        this._errorStyle = error_style;
    }
    
    @Override
    public int getErrorStyle() {
        return this._errorStyle;
    }
    
    @Override
    public void setEmptyCellAllowed(final boolean allowed) {
        this._emptyCellAllowed = allowed;
    }
    
    @Override
    public boolean getEmptyCellAllowed() {
        return this._emptyCellAllowed;
    }
    
    @Override
    public void setSuppressDropDownArrow(final boolean suppress) {
        this._suppress_dropdown_arrow = suppress;
    }
    
    @Override
    public boolean getSuppressDropDownArrow() {
        return this._constraint.getValidationType() == 3 && this._suppress_dropdown_arrow;
    }
    
    @Override
    public void setShowPromptBox(final boolean show) {
        this._showPromptBox = show;
    }
    
    @Override
    public boolean getShowPromptBox() {
        return this._showPromptBox;
    }
    
    @Override
    public void setShowErrorBox(final boolean show) {
        this._showErrorBox = show;
    }
    
    @Override
    public boolean getShowErrorBox() {
        return this._showErrorBox;
    }
    
    @Override
    public void createPromptBox(final String title, final String text) {
        this._prompt_title = title;
        this._prompt_text = text;
        this.setShowPromptBox(true);
    }
    
    @Override
    public String getPromptBoxTitle() {
        return this._prompt_title;
    }
    
    @Override
    public String getPromptBoxText() {
        return this._prompt_text;
    }
    
    @Override
    public void createErrorBox(final String title, final String text) {
        this._error_title = title;
        this._error_text = text;
        this.setShowErrorBox(true);
    }
    
    @Override
    public String getErrorBoxTitle() {
        return this._error_title;
    }
    
    @Override
    public String getErrorBoxText() {
        return this._error_text;
    }
    
    public DVRecord createDVRecord(final HSSFSheet sheet) {
        final DVConstraint.FormulaPair fp = this._constraint.createFormulas(sheet);
        return new DVRecord(this._constraint.getValidationType(), this._constraint.getOperator(), this._errorStyle, this._emptyCellAllowed, this.getSuppressDropDownArrow(), this._constraint.getValidationType() == 3 && this._constraint.getExplicitListValues() != null, this._showPromptBox, this._prompt_title, this._prompt_text, this._showErrorBox, this._error_title, this._error_text, fp.getFormula1(), fp.getFormula2(), this._regions);
    }
}
