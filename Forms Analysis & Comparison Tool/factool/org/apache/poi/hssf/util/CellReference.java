// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.util;

public final class CellReference extends org.apache.poi.ss.util.CellReference
{
    public CellReference(final String cellRef) {
        super(cellRef);
    }
    
    public CellReference(final int pRow, final int pCol) {
        super(pRow, pCol, true, true);
    }
    
    public CellReference(final int pRow, final int pCol, final boolean pAbsRow, final boolean pAbsCol) {
        super(null, pRow, pCol, pAbsRow, pAbsCol);
    }
    
    public CellReference(final String pSheetName, final int pRow, final int pCol, final boolean pAbsRow, final boolean pAbsCol) {
        super(pSheetName, pRow, pCol, pAbsRow, pAbsCol);
    }
}
