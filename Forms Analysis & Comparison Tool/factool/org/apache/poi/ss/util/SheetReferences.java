// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.util;

import java.util.HashMap;
import java.util.Map;

public class SheetReferences
{
    Map<Integer, String> map;
    
    public SheetReferences() {
        this.map = new HashMap<Integer, String>(5);
    }
    
    public void addSheetReference(final String sheetName, final int number) {
        this.map.put(number, sheetName);
    }
    
    public String getSheetName(final int number) {
        return this.map.get(number);
    }
}
