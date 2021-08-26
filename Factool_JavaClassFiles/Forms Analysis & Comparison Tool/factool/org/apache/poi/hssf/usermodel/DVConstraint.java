// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import org.apache.poi.ss.formula.FormulaRenderer;
import java.text.MessageFormat;
import java.util.regex.Pattern;
import org.apache.poi.ss.formula.FormulaRenderingWorkbook;
import org.apache.poi.hssf.record.DVRecord;
import org.apache.poi.ss.formula.ptg.NumberPtg;
import org.apache.poi.ss.formula.ptg.StringPtg;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.formula.ptg.Ptg;
import java.util.Date;
import java.text.ParseException;
import org.apache.poi.ss.usermodel.DateUtil;
import java.text.SimpleDateFormat;
import org.apache.poi.ss.usermodel.DataValidationConstraint;

public class DVConstraint implements DataValidationConstraint
{
    private static final ValidationType VT;
    private final int _validationType;
    private int _operator;
    private String[] _explicitListValues;
    private String _formula1;
    private String _formula2;
    private Double _value1;
    private Double _value2;
    
    private DVConstraint(final int validationType, final int comparisonOperator, final String formulaA, final String formulaB, final Double value1, final Double value2, final String[] excplicitListValues) {
        this._validationType = validationType;
        this._operator = comparisonOperator;
        this._formula1 = formulaA;
        this._formula2 = formulaB;
        this._value1 = value1;
        this._value2 = value2;
        this._explicitListValues = excplicitListValues;
    }
    
    private DVConstraint(final String listFormula, final String[] excplicitListValues) {
        this(3, 0, listFormula, null, null, null, excplicitListValues);
    }
    
    public static DVConstraint createNumericConstraint(final int validationType, final int comparisonOperator, final String expr1, final String expr2) {
        switch (validationType) {
            case 0: {
                if (expr1 != null || expr2 != null) {
                    throw new IllegalArgumentException("expr1 and expr2 must be null for validation type 'any'");
                }
                break;
            }
            case 1:
            case 2:
            case 6: {
                if (expr1 == null) {
                    throw new IllegalArgumentException("expr1 must be supplied");
                }
                OperatorType.validateSecondArg(comparisonOperator, expr2);
                break;
            }
            default: {
                throw new IllegalArgumentException("Validation Type (" + validationType + ") not supported with this method");
            }
        }
        final String formula1 = getFormulaFromTextExpression(expr1);
        final Double value1 = (formula1 == null) ? convertNumber(expr1) : null;
        final String formula2 = getFormulaFromTextExpression(expr2);
        final Double value2 = (formula2 == null) ? convertNumber(expr2) : null;
        return new DVConstraint(validationType, comparisonOperator, formula1, formula2, value1, value2, null);
    }
    
    public static DVConstraint createFormulaListConstraint(final String listFormula) {
        return new DVConstraint(listFormula, null);
    }
    
    public static DVConstraint createExplicitListConstraint(final String[] explicitListValues) {
        return new DVConstraint(null, explicitListValues);
    }
    
    public static DVConstraint createTimeConstraint(final int comparisonOperator, final String expr1, final String expr2) {
        if (expr1 == null) {
            throw new IllegalArgumentException("expr1 must be supplied");
        }
        OperatorType.validateSecondArg(comparisonOperator, expr1);
        final String formula1 = getFormulaFromTextExpression(expr1);
        final Double value1 = (formula1 == null) ? convertTime(expr1) : null;
        final String formula2 = getFormulaFromTextExpression(expr2);
        final Double value2 = (formula2 == null) ? convertTime(expr2) : null;
        return new DVConstraint(5, comparisonOperator, formula1, formula2, value1, value2, null);
    }
    
    public static DVConstraint createDateConstraint(final int comparisonOperator, final String expr1, final String expr2, final String dateFormat) {
        if (expr1 == null) {
            throw new IllegalArgumentException("expr1 must be supplied");
        }
        OperatorType.validateSecondArg(comparisonOperator, expr2);
        final SimpleDateFormat df = (dateFormat == null) ? null : new SimpleDateFormat(dateFormat);
        final String formula1 = getFormulaFromTextExpression(expr1);
        final Double value1 = (formula1 == null) ? convertDate(expr1, df) : null;
        final String formula2 = getFormulaFromTextExpression(expr2);
        final Double value2 = (formula2 == null) ? convertDate(expr2, df) : null;
        return new DVConstraint(4, comparisonOperator, formula1, formula2, value1, value2, null);
    }
    
    private static String getFormulaFromTextExpression(final String textExpr) {
        if (textExpr == null) {
            return null;
        }
        if (textExpr.length() < 1) {
            throw new IllegalArgumentException("Empty string is not a valid formula/value expression");
        }
        if (textExpr.charAt(0) == '=') {
            return textExpr.substring(1);
        }
        return null;
    }
    
    private static Double convertNumber(final String numberStr) {
        if (numberStr == null) {
            return null;
        }
        try {
            return new Double(numberStr);
        }
        catch (NumberFormatException e) {
            throw new RuntimeException("The supplied text '" + numberStr + "' could not be parsed as a number");
        }
    }
    
    private static Double convertTime(final String timeStr) {
        if (timeStr == null) {
            return null;
        }
        return new Double(DateUtil.convertTime(timeStr));
    }
    
    private static Double convertDate(final String dateStr, final SimpleDateFormat dateFormat) {
        if (dateStr == null) {
            return null;
        }
        Date dateVal;
        if (dateFormat == null) {
            dateVal = DateUtil.parseYYYYMMDDDate(dateStr);
        }
        else {
            try {
                dateVal = dateFormat.parse(dateStr);
            }
            catch (ParseException e) {
                throw new RuntimeException("Failed to parse date '" + dateStr + "' using specified format '" + dateFormat + "'", e);
            }
        }
        return new Double(DateUtil.getExcelDate(dateVal));
    }
    
    public static DVConstraint createCustomFormulaConstraint(final String formula) {
        if (formula == null) {
            throw new IllegalArgumentException("formula must be supplied");
        }
        return new DVConstraint(7, 0, formula, null, null, null, null);
    }
    
    @Override
    public int getValidationType() {
        return this._validationType;
    }
    
    public boolean isListValidationType() {
        return this._validationType == 3;
    }
    
    public boolean isExplicitList() {
        return this._validationType == 3 && this._explicitListValues != null;
    }
    
    @Override
    public int getOperator() {
        return this._operator;
    }
    
    @Override
    public void setOperator(final int operator) {
        this._operator = operator;
    }
    
    @Override
    public String[] getExplicitListValues() {
        return this._explicitListValues;
    }
    
    @Override
    public void setExplicitListValues(final String[] explicitListValues) {
        if (this._validationType != 3) {
            throw new RuntimeException("Cannot setExplicitListValues on non-list constraint");
        }
        this._formula1 = null;
        this._explicitListValues = explicitListValues;
    }
    
    @Override
    public String getFormula1() {
        return this._formula1;
    }
    
    @Override
    public void setFormula1(final String formula1) {
        this._value1 = null;
        this._explicitListValues = null;
        this._formula1 = formula1;
    }
    
    @Override
    public String getFormula2() {
        return this._formula2;
    }
    
    @Override
    public void setFormula2(final String formula2) {
        this._value2 = null;
        this._formula2 = formula2;
    }
    
    public Double getValue1() {
        return this._value1;
    }
    
    public void setValue1(final double value1) {
        this._formula1 = null;
        this._value1 = new Double(value1);
    }
    
    public Double getValue2() {
        return this._value2;
    }
    
    public void setValue2(final double value2) {
        this._formula2 = null;
        this._value2 = new Double(value2);
    }
    
    FormulaPair createFormulas(final HSSFSheet sheet) {
        Ptg[] formula1;
        Ptg[] formula2;
        if (this.isListValidationType()) {
            formula1 = this.createListFormula(sheet);
            formula2 = Ptg.EMPTY_PTG_ARRAY;
        }
        else {
            formula1 = convertDoubleFormula(this._formula1, this._value1, sheet);
            formula2 = convertDoubleFormula(this._formula2, this._value2, sheet);
        }
        return new FormulaPair(formula1, formula2);
    }
    
    private Ptg[] createListFormula(final HSSFSheet sheet) {
        if (this._explicitListValues == null) {
            final HSSFWorkbook wb = sheet.getWorkbook();
            return HSSFFormulaParser.parse(this._formula1, wb, 5, wb.getSheetIndex(sheet));
        }
        final StringBuffer sb = new StringBuffer(this._explicitListValues.length * 16);
        for (int i = 0; i < this._explicitListValues.length; ++i) {
            if (i > 0) {
                sb.append('\0');
            }
            sb.append(this._explicitListValues[i]);
        }
        return new Ptg[] { new StringPtg(sb.toString()) };
    }
    
    private static Ptg[] convertDoubleFormula(final String formula, final Double value, final HSSFSheet sheet) {
        if (formula == null) {
            if (value == null) {
                return Ptg.EMPTY_PTG_ARRAY;
            }
            return new Ptg[] { new NumberPtg(value) };
        }
        else {
            if (value != null) {
                throw new IllegalStateException("Both formula and value cannot be present");
            }
            final HSSFWorkbook wb = sheet.getWorkbook();
            return HSSFFormulaParser.parse(formula, wb, 0, wb.getSheetIndex(sheet));
        }
    }
    
    static DVConstraint createDVConstraint(final DVRecord dvRecord, final FormulaRenderingWorkbook book) {
        switch (dvRecord.getDataType()) {
            case 0: {
                return new DVConstraint(0, dvRecord.getConditionOperator(), null, null, null, null, null);
            }
            case 1:
            case 2:
            case 4:
            case 5:
            case 6: {
                final FormulaValuePair pair1 = toFormulaString(dvRecord.getFormula1(), book);
                final FormulaValuePair pair2 = toFormulaString(dvRecord.getFormula2(), book);
                return new DVConstraint(dvRecord.getDataType(), dvRecord.getConditionOperator(), pair1.formula(), pair2.formula(), pair1.value(), pair2.value(), null);
            }
            case 3: {
                if (dvRecord.getListExplicitFormula()) {
                    String values = toFormulaString(dvRecord.getFormula1(), book).string();
                    if (values.startsWith("\"")) {
                        values = values.substring(1);
                    }
                    if (values.endsWith("\"")) {
                        values = values.substring(0, values.length() - 1);
                    }
                    final String[] explicitListValues = values.split(Pattern.quote("\u0000"));
                    return createExplicitListConstraint(explicitListValues);
                }
                final String listFormula = toFormulaString(dvRecord.getFormula1(), book).string();
                return createFormulaListConstraint(listFormula);
            }
            case 7: {
                return createCustomFormulaConstraint(toFormulaString(dvRecord.getFormula1(), book).string());
            }
            default: {
                throw new UnsupportedOperationException(MessageFormat.format("validationType={0}", dvRecord.getDataType()));
            }
        }
    }
    
    private static FormulaValuePair toFormulaString(final Ptg[] ptgs, final FormulaRenderingWorkbook book) {
        final FormulaValuePair pair = new FormulaValuePair();
        if (ptgs != null && ptgs.length > 0) {
            final String string = FormulaRenderer.toFormulaString(book, ptgs);
            if (ptgs.length == 1 && ptgs[0].getClass() == NumberPtg.class) {
                pair._value = string;
            }
            else {
                pair._formula = string;
            }
        }
        return pair;
    }
    
    static {
        VT = null;
    }
    
    public static final class FormulaPair
    {
        private final Ptg[] _formula1;
        private final Ptg[] _formula2;
        
        public FormulaPair(final Ptg[] formula1, final Ptg[] formula2) {
            this._formula1 = formula1;
            this._formula2 = formula2;
        }
        
        public Ptg[] getFormula1() {
            return this._formula1;
        }
        
        public Ptg[] getFormula2() {
            return this._formula2;
        }
    }
    
    private static class FormulaValuePair
    {
        private String _formula;
        private String _value;
        
        public String formula() {
            return this._formula;
        }
        
        public Double value() {
            if (this._value == null) {
                return null;
            }
            return new Double(this._value);
        }
        
        public String string() {
            if (this._formula != null) {
                return this._formula;
            }
            if (this._value != null) {
                return this._value;
            }
            return null;
        }
    }
}
