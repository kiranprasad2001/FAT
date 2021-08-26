// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.format;

import java.awt.Color;

public class CellFormatResult
{
    public final boolean applies;
    public final String text;
    public final Color textColor;
    
    public CellFormatResult(final boolean applies, final String text, final Color textColor) {
        this.applies = applies;
        this.text = text;
        this.textColor = (applies ? textColor : null);
    }
}
