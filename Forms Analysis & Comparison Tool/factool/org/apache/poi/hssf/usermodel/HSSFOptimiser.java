// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import java.util.Iterator;
import org.apache.poi.hssf.record.ExtendedFormatRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.hssf.record.common.UnicodeString;
import java.util.HashSet;
import org.apache.poi.hssf.record.FontRecord;

public class HSSFOptimiser
{
    public static void optimiseFonts(final HSSFWorkbook workbook) {
        final short[] newPos = new short[workbook.getWorkbook().getNumberOfFontRecords() + 1];
        final boolean[] zapRecords = new boolean[newPos.length];
        for (int i = 0; i < newPos.length; ++i) {
            newPos[i] = (short)i;
            zapRecords[i] = false;
        }
        final FontRecord[] frecs = new FontRecord[newPos.length];
        for (int j = 0; j < newPos.length; ++j) {
            if (j != 4) {
                frecs[j] = workbook.getWorkbook().getFontRecordAt(j);
            }
        }
        for (int j = 5; j < newPos.length; ++j) {
            int earlierDuplicate = -1;
            for (int k = 0; k < j && earlierDuplicate == -1; ++k) {
                if (k != 4) {
                    final FontRecord frCheck = workbook.getWorkbook().getFontRecordAt(k);
                    if (frCheck.sameProperties(frecs[j])) {
                        earlierDuplicate = k;
                    }
                }
            }
            if (earlierDuplicate != -1) {
                newPos[j] = (short)earlierDuplicate;
                zapRecords[j] = true;
            }
        }
        for (int j = 5; j < newPos.length; ++j) {
            short newPosition;
            final short preDeletePos = newPosition = newPos[j];
            for (int l = 0; l < preDeletePos; ++l) {
                if (zapRecords[l]) {
                    --newPosition;
                }
            }
            newPos[j] = newPosition;
        }
        for (int j = 5; j < newPos.length; ++j) {
            if (zapRecords[j]) {
                workbook.getWorkbook().removeFontRecord(frecs[j]);
            }
        }
        workbook.resetFontCache();
        for (int j = 0; j < workbook.getWorkbook().getNumExFormats(); ++j) {
            final ExtendedFormatRecord xfr = workbook.getWorkbook().getExFormatAt(j);
            xfr.setFontIndex(newPos[xfr.getFontIndex()]);
        }
        final HashSet<UnicodeString> doneUnicodeStrings = new HashSet<UnicodeString>();
        for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); ++sheetNum) {
            final HSSFSheet s = workbook.getSheetAt(sheetNum);
            for (final Row row : s) {
                for (final Cell cell : row) {
                    if (cell.getCellType() == 1) {
                        final HSSFRichTextString rtr = (HSSFRichTextString)cell.getRichStringCellValue();
                        final UnicodeString u = rtr.getRawUnicodeString();
                        if (doneUnicodeStrings.contains(u)) {
                            continue;
                        }
                        for (short m = 5; m < newPos.length; ++m) {
                            if (m != newPos[m]) {
                                u.swapFontUse(m, newPos[m]);
                            }
                        }
                        doneUnicodeStrings.add(u);
                    }
                }
            }
        }
    }
    
    public static void optimiseCellStyles(final HSSFWorkbook workbook) {
        final short[] newPos = new short[workbook.getWorkbook().getNumExFormats()];
        final boolean[] isUsed = new boolean[newPos.length];
        final boolean[] zapRecords = new boolean[newPos.length];
        for (int i = 0; i < newPos.length; ++i) {
            isUsed[i] = false;
            newPos[i] = (short)i;
            zapRecords[i] = false;
        }
        final ExtendedFormatRecord[] xfrs = new ExtendedFormatRecord[newPos.length];
        for (int j = 0; j < newPos.length; ++j) {
            xfrs[j] = workbook.getWorkbook().getExFormatAt(j);
        }
        for (int j = 21; j < newPos.length; ++j) {
            int earlierDuplicate = -1;
            for (int k = 0; k < j && earlierDuplicate == -1; ++k) {
                final ExtendedFormatRecord xfCheck = workbook.getWorkbook().getExFormatAt(k);
                if (xfCheck.equals(xfrs[j])) {
                    earlierDuplicate = k;
                }
            }
            if (earlierDuplicate != -1) {
                newPos[j] = (short)earlierDuplicate;
                zapRecords[j] = true;
            }
            if (earlierDuplicate != -1) {
                isUsed[earlierDuplicate] = true;
            }
        }
        for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); ++sheetNum) {
            final HSSFSheet s = workbook.getSheetAt(sheetNum);
            for (final Row row : s) {
                for (final Cell cellI : row) {
                    final HSSFCell cell = (HSSFCell)cellI;
                    final short oldXf = cell.getCellValueRecord().getXFIndex();
                    isUsed[oldXf] = true;
                }
            }
        }
        for (int j = 21; j < isUsed.length; ++j) {
            if (!isUsed[j]) {
                zapRecords[j] = true;
                newPos[j] = 0;
            }
        }
        for (int j = 21; j < newPos.length; ++j) {
            short newPosition;
            final short preDeletePos = newPosition = newPos[j];
            for (int l = 0; l < preDeletePos; ++l) {
                if (zapRecords[l]) {
                    --newPosition;
                }
            }
            newPos[j] = newPosition;
        }
        int max = newPos.length;
        int removed = 0;
        for (int m = 21; m < max; ++m) {
            if (zapRecords[m + removed]) {
                workbook.getWorkbook().removeExFormatRecord(m);
                --m;
                --max;
                ++removed;
            }
        }
        for (int sheetNum2 = 0; sheetNum2 < workbook.getNumberOfSheets(); ++sheetNum2) {
            final HSSFSheet s2 = workbook.getSheetAt(sheetNum2);
            for (final Row row2 : s2) {
                for (final Cell cellI2 : row2) {
                    final HSSFCell cell2 = (HSSFCell)cellI2;
                    final short oldXf2 = cell2.getCellValueRecord().getXFIndex();
                    final HSSFCellStyle newStyle = workbook.getCellStyleAt(newPos[oldXf2]);
                    cell2.setCellStyle(newStyle);
                }
            }
        }
    }
}
