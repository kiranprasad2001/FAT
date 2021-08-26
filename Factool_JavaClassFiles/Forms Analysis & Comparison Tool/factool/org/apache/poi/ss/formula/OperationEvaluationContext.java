// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula;

import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.eval.FunctionNameEval;
import org.apache.poi.ss.formula.eval.ExternalNameEval;
import org.apache.poi.ss.formula.ptg.NameXPxg;
import org.apache.poi.ss.formula.ptg.NameXPtg;
import org.apache.poi.ss.formula.ptg.Area3DPxg;
import org.apache.poi.ss.formula.ptg.Area3DPtg;
import org.apache.poi.ss.formula.ptg.Ref3DPxg;
import org.apache.poi.ss.formula.ptg.Ref3DPtg;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.FreeRefFunction;

public final class OperationEvaluationContext
{
    public static final FreeRefFunction UDF;
    private final EvaluationWorkbook _workbook;
    private final int _sheetIndex;
    private final int _rowIndex;
    private final int _columnIndex;
    private final EvaluationTracker _tracker;
    private final WorkbookEvaluator _bookEvaluator;
    
    public OperationEvaluationContext(final WorkbookEvaluator bookEvaluator, final EvaluationWorkbook workbook, final int sheetIndex, final int srcRowNum, final int srcColNum, final EvaluationTracker tracker) {
        this._bookEvaluator = bookEvaluator;
        this._workbook = workbook;
        this._sheetIndex = sheetIndex;
        this._rowIndex = srcRowNum;
        this._columnIndex = srcColNum;
        this._tracker = tracker;
    }
    
    public EvaluationWorkbook getWorkbook() {
        return this._workbook;
    }
    
    public int getRowIndex() {
        return this._rowIndex;
    }
    
    public int getColumnIndex() {
        return this._columnIndex;
    }
    
    SheetRangeEvaluator createExternSheetRefEvaluator(final ExternSheetReferenceToken ptg) {
        return this.createExternSheetRefEvaluator(ptg.getExternSheetIndex());
    }
    
    SheetRangeEvaluator createExternSheetRefEvaluator(final String firstSheetName, final String lastSheetName, final int externalWorkbookNumber) {
        final EvaluationWorkbook.ExternalSheet externalSheet = this._workbook.getExternalSheet(firstSheetName, lastSheetName, externalWorkbookNumber);
        return this.createExternSheetRefEvaluator(externalSheet);
    }
    
    SheetRangeEvaluator createExternSheetRefEvaluator(final int externSheetIndex) {
        final EvaluationWorkbook.ExternalSheet externalSheet = this._workbook.getExternalSheet(externSheetIndex);
        return this.createExternSheetRefEvaluator(externalSheet);
    }
    
    SheetRangeEvaluator createExternSheetRefEvaluator(final EvaluationWorkbook.ExternalSheet externalSheet) {
        int otherLastSheetIndex = -1;
        WorkbookEvaluator targetEvaluator;
        int otherFirstSheetIndex;
        if (externalSheet == null || externalSheet.getWorkbookName() == null) {
            targetEvaluator = this._bookEvaluator;
            otherFirstSheetIndex = this._workbook.getSheetIndex(externalSheet.getSheetName());
            if (externalSheet instanceof EvaluationWorkbook.ExternalSheetRange) {
                final String lastSheetName = ((EvaluationWorkbook.ExternalSheetRange)externalSheet).getLastSheetName();
                otherLastSheetIndex = this._workbook.getSheetIndex(lastSheetName);
            }
        }
        else {
            final String workbookName = externalSheet.getWorkbookName();
            try {
                targetEvaluator = this._bookEvaluator.getOtherWorkbookEvaluator(workbookName);
            }
            catch (CollaboratingWorkbooksEnvironment.WorkbookNotFoundException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            otherFirstSheetIndex = targetEvaluator.getSheetIndex(externalSheet.getSheetName());
            if (externalSheet instanceof EvaluationWorkbook.ExternalSheetRange) {
                final String lastSheetName2 = ((EvaluationWorkbook.ExternalSheetRange)externalSheet).getLastSheetName();
                otherLastSheetIndex = targetEvaluator.getSheetIndex(lastSheetName2);
            }
            if (otherFirstSheetIndex < 0) {
                throw new RuntimeException("Invalid sheet name '" + externalSheet.getSheetName() + "' in bool '" + workbookName + "'.");
            }
        }
        if (otherLastSheetIndex == -1) {
            otherLastSheetIndex = otherFirstSheetIndex;
        }
        final SheetRefEvaluator[] evals = new SheetRefEvaluator[otherLastSheetIndex - otherFirstSheetIndex + 1];
        for (int i = 0; i < evals.length; ++i) {
            final int otherSheetIndex = i + otherFirstSheetIndex;
            evals[i] = new SheetRefEvaluator(targetEvaluator, this._tracker, otherSheetIndex);
        }
        return new SheetRangeEvaluator(otherFirstSheetIndex, otherLastSheetIndex, evals);
    }
    
    private SheetRefEvaluator createExternSheetRefEvaluator(final String workbookName, final String sheetName) {
        WorkbookEvaluator targetEvaluator;
        if (workbookName == null) {
            targetEvaluator = this._bookEvaluator;
        }
        else {
            if (sheetName == null) {
                throw new IllegalArgumentException("sheetName must not be null if workbookName is provided");
            }
            try {
                targetEvaluator = this._bookEvaluator.getOtherWorkbookEvaluator(workbookName);
            }
            catch (CollaboratingWorkbooksEnvironment.WorkbookNotFoundException e) {
                return null;
            }
        }
        final int otherSheetIndex = (sheetName == null) ? this._sheetIndex : targetEvaluator.getSheetIndex(sheetName);
        if (otherSheetIndex < 0) {
            return null;
        }
        return new SheetRefEvaluator(targetEvaluator, this._tracker, otherSheetIndex);
    }
    
    public SheetRangeEvaluator getRefEvaluatorForCurrentSheet() {
        final SheetRefEvaluator sre = new SheetRefEvaluator(this._bookEvaluator, this._tracker, this._sheetIndex);
        return new SheetRangeEvaluator(this._sheetIndex, sre);
    }
    
    public ValueEval getDynamicReference(final String workbookName, final String sheetName, final String refStrPart1, final String refStrPart2, final boolean isA1Style) {
        if (!isA1Style) {
            throw new RuntimeException("R1C1 style not supported yet");
        }
        final SheetRefEvaluator se = this.createExternSheetRefEvaluator(workbookName, sheetName);
        if (se == null) {
            return ErrorEval.REF_INVALID;
        }
        final SheetRangeEvaluator sre = new SheetRangeEvaluator(this._sheetIndex, se);
        final SpreadsheetVersion ssVersion = ((FormulaParsingWorkbook)this._workbook).getSpreadsheetVersion();
        final CellReference.NameType part1refType = classifyCellReference(refStrPart1, ssVersion);
        switch (part1refType) {
            case BAD_CELL_OR_NAMED_RANGE: {
                return ErrorEval.REF_INVALID;
            }
            case NAMED_RANGE: {
                final EvaluationName nm = ((FormulaParsingWorkbook)this._workbook).getName(refStrPart1, this._sheetIndex);
                if (!nm.isRange()) {
                    throw new RuntimeException("Specified name '" + refStrPart1 + "' is not a range as expected.");
                }
                return this._bookEvaluator.evaluateNameFormula(nm.getNameDefinition(), this);
            }
            default: {
                if (refStrPart2 == null) {
                    switch (part1refType) {
                        case COLUMN:
                        case ROW: {
                            return ErrorEval.REF_INVALID;
                        }
                        case CELL: {
                            final CellReference cr = new CellReference(refStrPart1);
                            return new LazyRefEval(cr.getRow(), cr.getCol(), sre);
                        }
                        default: {
                            throw new IllegalStateException("Unexpected reference classification of '" + refStrPart1 + "'.");
                        }
                    }
                }
                else {
                    final CellReference.NameType part2refType = classifyCellReference(refStrPart1, ssVersion);
                    switch (part2refType) {
                        case BAD_CELL_OR_NAMED_RANGE: {
                            return ErrorEval.REF_INVALID;
                        }
                        case NAMED_RANGE: {
                            throw new RuntimeException("Cannot evaluate '" + refStrPart1 + "'. Indirect evaluation of defined names not supported yet");
                        }
                        default: {
                            if (part2refType != part1refType) {
                                return ErrorEval.REF_INVALID;
                            }
                            int firstRow = 0;
                            int lastRow = 0;
                            int firstCol = 0;
                            int lastCol = 0;
                            switch (part1refType) {
                                case COLUMN: {
                                    firstRow = 0;
                                    if (part2refType.equals(CellReference.NameType.COLUMN)) {
                                        lastRow = ssVersion.getLastRowIndex();
                                        firstCol = parseRowRef(refStrPart1);
                                        lastCol = parseRowRef(refStrPart2);
                                        break;
                                    }
                                    lastRow = ssVersion.getLastRowIndex();
                                    firstCol = parseColRef(refStrPart1);
                                    lastCol = parseColRef(refStrPart2);
                                    break;
                                }
                                case ROW: {
                                    firstCol = 0;
                                    if (part2refType.equals(CellReference.NameType.ROW)) {
                                        firstRow = parseColRef(refStrPart1);
                                        lastRow = parseColRef(refStrPart2);
                                        lastCol = ssVersion.getLastColumnIndex();
                                        break;
                                    }
                                    lastCol = ssVersion.getLastColumnIndex();
                                    firstRow = parseRowRef(refStrPart1);
                                    lastRow = parseRowRef(refStrPart2);
                                    break;
                                }
                                case CELL: {
                                    CellReference cr2 = new CellReference(refStrPart1);
                                    firstRow = cr2.getRow();
                                    firstCol = cr2.getCol();
                                    cr2 = new CellReference(refStrPart2);
                                    lastRow = cr2.getRow();
                                    lastCol = cr2.getCol();
                                    break;
                                }
                                default: {
                                    throw new IllegalStateException("Unexpected reference classification of '" + refStrPart1 + "'.");
                                }
                            }
                            return new LazyAreaEval(firstRow, firstCol, lastRow, lastCol, sre);
                        }
                    }
                }
                break;
            }
        }
    }
    
    private static int parseRowRef(final String refStrPart) {
        return CellReference.convertColStringToIndex(refStrPart);
    }
    
    private static int parseColRef(final String refStrPart) {
        return Integer.parseInt(refStrPart) - 1;
    }
    
    private static CellReference.NameType classifyCellReference(final String str, final SpreadsheetVersion ssVersion) {
        final int len = str.length();
        if (len < 1) {
            return CellReference.NameType.BAD_CELL_OR_NAMED_RANGE;
        }
        return CellReference.classifyCellReference(str, ssVersion);
    }
    
    public FreeRefFunction findUserDefinedFunction(final String functionName) {
        return this._bookEvaluator.findUserDefinedFunction(functionName);
    }
    
    public ValueEval getRefEval(final int rowIndex, final int columnIndex) {
        final SheetRangeEvaluator sre = this.getRefEvaluatorForCurrentSheet();
        return new LazyRefEval(rowIndex, columnIndex, sre);
    }
    
    public ValueEval getRef3DEval(final Ref3DPtg rptg) {
        final SheetRangeEvaluator sre = this.createExternSheetRefEvaluator(rptg.getExternSheetIndex());
        return new LazyRefEval(rptg.getRow(), rptg.getColumn(), sre);
    }
    
    public ValueEval getRef3DEval(final Ref3DPxg rptg) {
        final SheetRangeEvaluator sre = this.createExternSheetRefEvaluator(rptg.getSheetName(), rptg.getLastSheetName(), rptg.getExternalWorkbookNumber());
        return new LazyRefEval(rptg.getRow(), rptg.getColumn(), sre);
    }
    
    public ValueEval getAreaEval(final int firstRowIndex, final int firstColumnIndex, final int lastRowIndex, final int lastColumnIndex) {
        final SheetRangeEvaluator sre = this.getRefEvaluatorForCurrentSheet();
        return new LazyAreaEval(firstRowIndex, firstColumnIndex, lastRowIndex, lastColumnIndex, sre);
    }
    
    public ValueEval getArea3DEval(final Area3DPtg aptg) {
        final SheetRangeEvaluator sre = this.createExternSheetRefEvaluator(aptg.getExternSheetIndex());
        return new LazyAreaEval(aptg.getFirstRow(), aptg.getFirstColumn(), aptg.getLastRow(), aptg.getLastColumn(), sre);
    }
    
    public ValueEval getArea3DEval(final Area3DPxg aptg) {
        final SheetRangeEvaluator sre = this.createExternSheetRefEvaluator(aptg.getSheetName(), aptg.getLastSheetName(), aptg.getExternalWorkbookNumber());
        return new LazyAreaEval(aptg.getFirstRow(), aptg.getFirstColumn(), aptg.getLastRow(), aptg.getLastColumn(), sre);
    }
    
    public ValueEval getNameXEval(final NameXPtg nameXPtg) {
        final EvaluationWorkbook.ExternalSheet externSheet = this._workbook.getExternalSheet(nameXPtg.getSheetRefIndex());
        if (externSheet == null || externSheet.getWorkbookName() == null) {
            return this.getLocalNameXEval(nameXPtg);
        }
        final String workbookName = externSheet.getWorkbookName();
        final EvaluationWorkbook.ExternalName externName = this._workbook.getExternalName(nameXPtg.getSheetRefIndex(), nameXPtg.getNameIndex());
        return this.getExternalNameXEval(externName, workbookName);
    }
    
    public ValueEval getNameXEval(final NameXPxg nameXPxg) {
        final EvaluationWorkbook.ExternalSheet externSheet = this._workbook.getExternalSheet(nameXPxg.getSheetName(), null, nameXPxg.getExternalWorkbookNumber());
        if (externSheet == null || externSheet.getWorkbookName() == null) {
            return this.getLocalNameXEval(nameXPxg);
        }
        final String workbookName = externSheet.getWorkbookName();
        final EvaluationWorkbook.ExternalName externName = this._workbook.getExternalName(nameXPxg.getNameName(), nameXPxg.getSheetName(), nameXPxg.getExternalWorkbookNumber());
        return this.getExternalNameXEval(externName, workbookName);
    }
    
    private ValueEval getLocalNameXEval(final NameXPxg nameXPxg) {
        int sIdx = -1;
        if (nameXPxg.getSheetName() != null) {
            sIdx = this._workbook.getSheetIndex(nameXPxg.getSheetName());
        }
        final String name = nameXPxg.getNameName();
        final EvaluationName evalName = this._workbook.getName(name, sIdx);
        if (evalName != null) {
            return new ExternalNameEval(evalName);
        }
        return new FunctionNameEval(name);
    }
    
    private ValueEval getLocalNameXEval(final NameXPtg nameXPtg) {
        final String name = this._workbook.resolveNameXText(nameXPtg);
        final int sheetNameAt = name.indexOf(33);
        EvaluationName evalName = null;
        if (sheetNameAt > -1) {
            final String sheetName = name.substring(0, sheetNameAt);
            final String nameName = name.substring(sheetNameAt + 1);
            evalName = this._workbook.getName(nameName, this._workbook.getSheetIndex(sheetName));
        }
        else {
            evalName = this._workbook.getName(name, -1);
        }
        if (evalName != null) {
            return new ExternalNameEval(evalName);
        }
        return new FunctionNameEval(name);
    }
    
    private ValueEval getExternalNameXEval(final EvaluationWorkbook.ExternalName externName, final String workbookName) {
        try {
            final WorkbookEvaluator refWorkbookEvaluator = this._bookEvaluator.getOtherWorkbookEvaluator(workbookName);
            final EvaluationName evaluationName = refWorkbookEvaluator.getName(externName.getName(), externName.getIx() - 1);
            if (evaluationName != null && evaluationName.hasFormula()) {
                if (evaluationName.getNameDefinition().length > 1) {
                    throw new RuntimeException("Complex name formulas not supported yet");
                }
                final OperationEvaluationContext refWorkbookContext = new OperationEvaluationContext(refWorkbookEvaluator, refWorkbookEvaluator.getWorkbook(), -1, -1, -1, this._tracker);
                final Ptg ptg = evaluationName.getNameDefinition()[0];
                if (ptg instanceof Ref3DPtg) {
                    final Ref3DPtg ref3D = (Ref3DPtg)ptg;
                    return refWorkbookContext.getRef3DEval(ref3D);
                }
                if (ptg instanceof Ref3DPxg) {
                    final Ref3DPxg ref3D2 = (Ref3DPxg)ptg;
                    return refWorkbookContext.getRef3DEval(ref3D2);
                }
                if (ptg instanceof Area3DPtg) {
                    final Area3DPtg area3D = (Area3DPtg)ptg;
                    return refWorkbookContext.getArea3DEval(area3D);
                }
                if (ptg instanceof Area3DPxg) {
                    final Area3DPxg area3D2 = (Area3DPxg)ptg;
                    return refWorkbookContext.getArea3DEval(area3D2);
                }
            }
            return ErrorEval.REF_INVALID;
        }
        catch (CollaboratingWorkbooksEnvironment.WorkbookNotFoundException wnfe) {
            return ErrorEval.REF_INVALID;
        }
    }
    
    static {
        UDF = UserDefinedFunction.instance;
    }
}
