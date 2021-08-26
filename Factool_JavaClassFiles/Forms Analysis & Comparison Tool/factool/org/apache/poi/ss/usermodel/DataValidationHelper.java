// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

import org.apache.poi.ss.util.CellRangeAddressList;

public interface DataValidationHelper
{
    DataValidationConstraint createFormulaListConstraint(final String p0);
    
    DataValidationConstraint createExplicitListConstraint(final String[] p0);
    
    DataValidationConstraint createNumericConstraint(final int p0, final int p1, final String p2, final String p3);
    
    DataValidationConstraint createTextLengthConstraint(final int p0, final String p1, final String p2);
    
    DataValidationConstraint createDecimalConstraint(final int p0, final String p1, final String p2);
    
    DataValidationConstraint createIntegerConstraint(final int p0, final String p1, final String p2);
    
    DataValidationConstraint createDateConstraint(final int p0, final String p1, final String p2, final String p3);
    
    DataValidationConstraint createTimeConstraint(final int p0, final String p1, final String p2);
    
    DataValidationConstraint createCustomConstraint(final String p0);
    
    DataValidation createValidation(final DataValidationConstraint p0, final CellRangeAddressList p1);
}
