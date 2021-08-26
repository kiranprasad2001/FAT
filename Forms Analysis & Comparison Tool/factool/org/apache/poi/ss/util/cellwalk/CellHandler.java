// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.util.cellwalk;

import org.apache.poi.ss.usermodel.Cell;

public interface CellHandler
{
    void onCell(final Cell p0, final CellWalkContext p1);
}
