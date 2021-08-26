// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

import java.util.Iterator;

public interface Row extends Iterable<Cell>
{
    public static final MissingCellPolicy RETURN_NULL_AND_BLANK = new MissingCellPolicy();
    public static final MissingCellPolicy RETURN_BLANK_AS_NULL = new MissingCellPolicy();
    public static final MissingCellPolicy CREATE_NULL_AS_BLANK = new MissingCellPolicy();
    
    Cell createCell(final int p0);
    
    Cell createCell(final int p0, final int p1);
    
    void removeCell(final Cell p0);
    
    void setRowNum(final int p0);
    
    int getRowNum();
    
    Cell getCell(final int p0);
    
    Cell getCell(final int p0, final MissingCellPolicy p1);
    
    short getFirstCellNum();
    
    short getLastCellNum();
    
    int getPhysicalNumberOfCells();
    
    void setHeight(final short p0);
    
    void setZeroHeight(final boolean p0);
    
    boolean getZeroHeight();
    
    void setHeightInPoints(final float p0);
    
    short getHeight();
    
    float getHeightInPoints();
    
    boolean isFormatted();
    
    CellStyle getRowStyle();
    
    void setRowStyle(final CellStyle p0);
    
    Iterator<Cell> cellIterator();
    
    Sheet getSheet();
    
    int getOutlineLevel();
    
    public static final class MissingCellPolicy
    {
        private static int NEXT_ID;
        public final int id;
        
        private MissingCellPolicy() {
            this.id = MissingCellPolicy.NEXT_ID++;
        }
        
        static {
            MissingCellPolicy.NEXT_ID = 1;
        }
    }
}
