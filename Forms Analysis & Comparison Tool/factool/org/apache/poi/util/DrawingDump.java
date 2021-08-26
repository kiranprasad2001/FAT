// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.util;

import java.io.IOException;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import java.io.InputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import java.io.FileInputStream;

public class DrawingDump
{
    public static void main(final String[] args) throws IOException {
        final POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(args[0]));
        final HSSFWorkbook wb = new HSSFWorkbook(fs);
        try {
            System.out.println("Drawing group:");
            wb.dumpDrawingGroupRecords(true);
            for (int sheetNum = 1; sheetNum <= wb.getNumberOfSheets(); ++sheetNum) {
                System.out.println("Sheet " + sheetNum + ":");
                final HSSFSheet sheet = wb.getSheetAt(sheetNum - 1);
                sheet.dumpDrawingRecords(true);
            }
        }
        finally {
            wb.close();
        }
    }
}
