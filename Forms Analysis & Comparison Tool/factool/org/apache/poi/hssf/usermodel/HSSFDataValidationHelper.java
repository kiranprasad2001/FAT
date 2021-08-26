// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;

public class HSSFDataValidationHelper implements DataValidationHelper
{
    private HSSFSheet sheet;
    
    public HSSFDataValidationHelper(final HSSFSheet sheet) {
        this.sheet = sheet;
    }
    
    @Override
    public DataValidationConstraint createDateConstraint(final int operatorType, final String formula1, final String formula2, final String dateFormat) {
        return DVConstraint.createDateConstraint(operatorType, formula1, formula2, dateFormat);
    }
    
    @Override
    public DataValidationConstraint createExplicitListConstraint(final String[] listOfValues) {
        return DVConstraint.createExplicitListConstraint(listOfValues);
    }
    
    @Override
    public DataValidationConstraint createFormulaListConstraint(final String listFormula) {
        return DVConstraint.createFormulaListConstraint(listFormula);
    }
    
    @Override
    public DataValidationConstraint createNumericConstraint(final int validationType, final int operatorType, final String formula1, final String formula2) {
        return DVConstraint.createNumericConstraint(validationType, operatorType, formula1, formula2);
    }
    
    @Override
    public DataValidationConstraint createIntegerConstraint(final int operatorType, final String formula1, final String formula2) {
        return DVConstraint.createNumericConstraint(1, operatorType, formula1, formula2);
    }
    
    @Override
    public DataValidationConstraint createDecimalConstraint(final int operatorType, final String formula1, final String formula2) {
        return DVConstraint.createNumericConstraint(2, operatorType, formula1, formula2);
    }
    
    @Override
    public DataValidationConstraint createTextLengthConstraint(final int operatorType, final String formula1, final String formula2) {
        return DVConstraint.createNumericConstraint(6, operatorType, formula1, formula2);
    }
    
    @Override
    public DataValidationConstraint createTimeConstraint(final int operatorType, final String formula1, final String formula2) {
        return DVConstraint.createTimeConstraint(operatorType, formula1, formula2);
    }
    
    @Override
    public DataValidationConstraint createCustomConstraint(final String formula) {
        return DVConstraint.createCustomFormulaConstraint(formula);
    }
    
    @Override
    public DataValidation createValidation(final DataValidationConstraint constraint, final CellRangeAddressList cellRangeAddressList) {
        return new HSSFDataValidation(cellRangeAddressList, constraint);
    }
}
