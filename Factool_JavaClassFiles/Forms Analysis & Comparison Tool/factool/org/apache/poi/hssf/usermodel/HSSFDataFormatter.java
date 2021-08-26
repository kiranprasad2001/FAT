// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import java.util.Locale;
import org.apache.poi.ss.usermodel.DataFormatter;

public final class HSSFDataFormatter extends DataFormatter
{
    public HSSFDataFormatter(final Locale locale) {
        super(locale);
    }
    
    public HSSFDataFormatter() {
        this(Locale.getDefault());
    }
}
