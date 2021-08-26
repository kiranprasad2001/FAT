// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.ptg;

import org.apache.poi.ss.formula.EvaluationWorkbook;
import org.apache.poi.ss.formula.SheetNameFormatter;
import org.apache.poi.ss.formula.FormulaRenderingWorkbook;

final class ExternSheetNameResolver
{
    private ExternSheetNameResolver() {
    }
    
    public static String prependSheetName(final FormulaRenderingWorkbook book, final int field_1_index_extern_sheet, final String cellRefText) {
        final EvaluationWorkbook.ExternalSheet externalSheet = book.getExternalSheet(field_1_index_extern_sheet);
        StringBuffer sb;
        if (externalSheet != null) {
            final String wbName = externalSheet.getWorkbookName();
            final String sheetName = externalSheet.getSheetName();
            if (wbName != null) {
                sb = new StringBuffer(wbName.length() + sheetName.length() + cellRefText.length() + 4);
                SheetNameFormatter.appendFormat(sb, wbName, sheetName);
            }
            else {
                sb = new StringBuffer(sheetName.length() + cellRefText.length() + 4);
                SheetNameFormatter.appendFormat(sb, sheetName);
            }
            if (externalSheet instanceof EvaluationWorkbook.ExternalSheetRange) {
                final EvaluationWorkbook.ExternalSheetRange r = (EvaluationWorkbook.ExternalSheetRange)externalSheet;
                if (!r.getFirstSheetName().equals(r.getLastSheetName())) {
                    sb.append(':');
                    SheetNameFormatter.appendFormat(sb, r.getLastSheetName());
                }
            }
        }
        else {
            final String firstSheetName = book.getSheetFirstNameByExternSheet(field_1_index_extern_sheet);
            final String lastSheetName = book.getSheetLastNameByExternSheet(field_1_index_extern_sheet);
            sb = new StringBuffer(firstSheetName.length() + cellRefText.length() + 4);
            if (firstSheetName.length() < 1) {
                sb.append("#REF");
            }
            else {
                SheetNameFormatter.appendFormat(sb, firstSheetName);
                if (!firstSheetName.equals(lastSheetName)) {
                    sb.append(':');
                    sb.append(lastSheetName);
                }
            }
        }
        sb.append('!');
        sb.append(cellRefText);
        return sb.toString();
    }
}
