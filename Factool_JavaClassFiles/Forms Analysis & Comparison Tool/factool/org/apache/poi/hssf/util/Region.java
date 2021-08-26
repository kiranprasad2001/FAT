// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.util;

public class Region extends org.apache.poi.ss.util.Region
{
    public Region() {
    }
    
    public Region(final int rowFrom, final short colFrom, final int rowTo, final short colTo) {
        super(rowFrom, colFrom, rowTo, colTo);
    }
    
    public Region(final String ref) {
        super(ref);
    }
}
