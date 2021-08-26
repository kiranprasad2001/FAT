// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.util;

public final class AreaReference extends org.apache.poi.ss.util.AreaReference
{
    public AreaReference(final String reference) {
        super(reference);
    }
    
    public AreaReference(final CellReference topLeft, final CellReference botRight) {
        super(topLeft, botRight);
    }
}
