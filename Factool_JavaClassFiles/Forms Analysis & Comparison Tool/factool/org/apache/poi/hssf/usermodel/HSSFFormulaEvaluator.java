// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.StringValueEval;
import org.apache.poi.ss.formula.eval.NumericValueEval;
import org.apache.poi.ss.formula.eval.BoolEval;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.formula.EvaluationCell;
import java.util.Map;
import org.apache.poi.ss.formula.CollaboratingWorkbooksEnvironment;
import org.apache.poi.ss.formula.EvaluationWorkbook;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.apache.poi.ss.formula.IStabilityClassifier;
import org.apache.poi.ss.formula.WorkbookEvaluator;
import org.apache.poi.ss.formula.WorkbookEvaluatorProvider;
import org.apache.poi.ss.usermodel.FormulaEvaluator;

public class HSSFFormulaEvaluator implements FormulaEvaluator, WorkbookEvaluatorProvider
{
    private WorkbookEvaluator _bookEvaluator;
    private HSSFWorkbook _book;
    
    @Deprecated
    public HSSFFormulaEvaluator(final HSSFSheet sheet, final HSSFWorkbook workbook) {
        this(workbook);
        this._book = workbook;
    }
    
    public HSSFFormulaEvaluator(final HSSFWorkbook workbook) {
        this(workbook, null);
        this._book = workbook;
    }
    
    public HSSFFormulaEvaluator(final HSSFWorkbook workbook, final IStabilityClassifier stabilityClassifier) {
        this(workbook, stabilityClassifier, null);
    }
    
    private HSSFFormulaEvaluator(final HSSFWorkbook workbook, final IStabilityClassifier stabilityClassifier, final UDFFinder udfFinder) {
        this._bookEvaluator = new WorkbookEvaluator(HSSFEvaluationWorkbook.create(workbook), stabilityClassifier, udfFinder);
    }
    
    public static HSSFFormulaEvaluator create(final HSSFWorkbook workbook, final IStabilityClassifier stabilityClassifier, final UDFFinder udfFinder) {
        return new HSSFFormulaEvaluator(workbook, stabilityClassifier, udfFinder);
    }
    
    public static void setupEnvironment(final String[] workbookNames, final HSSFFormulaEvaluator[] evaluators) {
        final WorkbookEvaluator[] wbEvals = new WorkbookEvaluator[evaluators.length];
        for (int i = 0; i < wbEvals.length; ++i) {
            wbEvals[i] = evaluators[i]._bookEvaluator;
        }
        CollaboratingWorkbooksEnvironment.setup(workbookNames, wbEvals);
    }
    
    @Override
    public void setupReferencedWorkbooks(final Map<String, FormulaEvaluator> evaluators) {
        CollaboratingWorkbooksEnvironment.setupFormulaEvaluator(evaluators);
    }
    
    @Override
    public WorkbookEvaluator _getWorkbookEvaluator() {
        return this._bookEvaluator;
    }
    
    @Deprecated
    public void setCurrentRow(final HSSFRow row) {
    }
    
    @Override
    public void clearAllCachedResultValues() {
        this._bookEvaluator.clearAllCachedResultValues();
    }
    
    public void notifyUpdateCell(final HSSFCell cell) {
        this._bookEvaluator.notifyUpdateCell(new HSSFEvaluationCell(cell));
    }
    
    @Override
    public void notifyUpdateCell(final Cell cell) {
        this._bookEvaluator.notifyUpdateCell(new HSSFEvaluationCell((HSSFCell)cell));
    }
    
    public void notifyDeleteCell(final HSSFCell cell) {
        this._bookEvaluator.notifyDeleteCell(new HSSFEvaluationCell(cell));
    }
    
    @Override
    public void notifyDeleteCell(final Cell cell) {
        this._bookEvaluator.notifyDeleteCell(new HSSFEvaluationCell((HSSFCell)cell));
    }
    
    @Override
    public void notifySetFormula(final Cell cell) {
        this._bookEvaluator.notifyUpdateCell(new HSSFEvaluationCell((HSSFCell)cell));
    }
    
    @Override
    public CellValue evaluate(final Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case 4: {
                return CellValue.valueOf(cell.getBooleanCellValue());
            }
            case 5: {
                return CellValue.getError(cell.getErrorCellValue());
            }
            case 2: {
                return this.evaluateFormulaCellValue(cell);
            }
            case 0: {
                return new CellValue(cell.getNumericCellValue());
            }
            case 1: {
                return new CellValue(cell.getRichStringCellValue().getString());
            }
            case 3: {
                return null;
            }
            default: {
                throw new IllegalStateException("Bad cell type (" + cell.getCellType() + ")");
            }
        }
    }
    
    @Override
    public int evaluateFormulaCell(final Cell cell) {
        if (cell == null || cell.getCellType() != 2) {
            return -1;
        }
        final CellValue cv = this.evaluateFormulaCellValue(cell);
        setCellValue(cell, cv);
        return cv.getCellType();
    }
    
    @Override
    public HSSFCell evaluateInCell(final Cell cell) {
        if (cell == null) {
            return null;
        }
        final HSSFCell result = (HSSFCell)cell;
        if (cell.getCellType() == 2) {
            final CellValue cv = this.evaluateFormulaCellValue(cell);
            setCellValue(cell, cv);
            setCellType(cell, cv);
        }
        return result;
    }
    
    private static void setCellType(final Cell cell, final CellValue cv) {
        final int cellType = cv.getCellType();
        switch (cellType) {
            case 0:
            case 1:
            case 4:
            case 5: {
                cell.setCellType(cellType);
            }
            default: {
                throw new IllegalStateException("Unexpected cell value type (" + cellType + ")");
            }
        }
    }
    
    private static void setCellValue(final Cell cell, final CellValue cv) {
        final int cellType = cv.getCellType();
        switch (cellType) {
            case 4: {
                cell.setCellValue(cv.getBooleanValue());
                break;
            }
            case 5: {
                cell.setCellErrorValue(cv.getErrorValue());
                break;
            }
            case 0: {
                cell.setCellValue(cv.getNumberValue());
                break;
            }
            case 1: {
                cell.setCellValue(new HSSFRichTextString(cv.getStringValue()));
                break;
            }
            default: {
                throw new IllegalStateException("Unexpected cell value type (" + cellType + ")");
            }
        }
    }
    
    public static void evaluateAllFormulaCells(final HSSFWorkbook wb) {
        evaluateAllFormulaCells(wb, new HSSFFormulaEvaluator(wb));
    }
    
    public static void evaluateAllFormulaCells(final Workbook wb) {
        final FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
        evaluateAllFormulaCells(wb, evaluator);
    }
    
    private static void evaluateAllFormulaCells(final Workbook wb, final FormulaEvaluator evaluator) {
        for (int i = 0; i < wb.getNumberOfSheets(); ++i) {
            final Sheet sheet = wb.getSheetAt(i);
            for (final Row r : sheet) {
                for (final Cell c : r) {
                    if (c.getCellType() == 2) {
                        evaluator.evaluateFormulaCell(c);
                    }
                }
            }
        }
    }
    
    @Override
    public void evaluateAll() {
        evaluateAllFormulaCells(this._book, this);
    }
    
    private CellValue evaluateFormulaCellValue(final Cell cell) {
        final ValueEval eval = this._bookEvaluator.evaluate(new HSSFEvaluationCell((HSSFCell)cell));
        if (eval instanceof BoolEval) {
            final BoolEval be = (BoolEval)eval;
            return CellValue.valueOf(be.getBooleanValue());
        }
        if (eval instanceof NumericValueEval) {
            final NumericValueEval ne = (NumericValueEval)eval;
            return new CellValue(ne.getNumberValue());
        }
        if (eval instanceof StringValueEval) {
            final StringValueEval ne2 = (StringValueEval)eval;
            return new CellValue(ne2.getStringValue());
        }
        if (eval instanceof ErrorEval) {
            return CellValue.getError(((ErrorEval)eval).getErrorCode());
        }
        throw new RuntimeException("Unexpected eval class (" + eval.getClass().getName() + ")");
    }
    
    @Override
    public void setIgnoreMissingWorkbooks(final boolean ignore) {
        this._bookEvaluator.setIgnoreMissingWorkbooks(ignore);
    }
    
    @Override
    public void setDebugEvaluationOutputForNextEval(final boolean value) {
        this._bookEvaluator.setDebugEvaluationOutputForNextEval(value);
    }
}
