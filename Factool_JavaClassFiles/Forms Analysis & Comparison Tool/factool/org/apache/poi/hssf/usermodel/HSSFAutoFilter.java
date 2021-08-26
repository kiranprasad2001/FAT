// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import org.apache.poi.ss.usermodel.AutoFilter;

public final class HSSFAutoFilter implements AutoFilter
{
    private HSSFSheet _sheet;
    
    HSSFAutoFilter(final HSSFSheet sheet) {
        this._sheet = sheet;
    }
}
