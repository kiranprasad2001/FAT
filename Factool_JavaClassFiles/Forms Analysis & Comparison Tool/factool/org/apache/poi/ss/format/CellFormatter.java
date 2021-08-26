// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.format;

import java.util.logging.Logger;
import java.util.Locale;

public abstract class CellFormatter
{
    protected final String format;
    public static final Locale LOCALE;
    static final Logger logger;
    
    public CellFormatter(final String format) {
        this.format = format;
    }
    
    public abstract void formatValue(final StringBuffer p0, final Object p1);
    
    public abstract void simpleValue(final StringBuffer p0, final Object p1);
    
    public String format(final Object value) {
        final StringBuffer sb = new StringBuffer();
        this.formatValue(sb, value);
        return sb.toString();
    }
    
    public String simpleFormat(final Object value) {
        final StringBuffer sb = new StringBuffer();
        this.simpleValue(sb, value);
        return sb.toString();
    }
    
    static String quote(final String str) {
        return '\"' + str + '\"';
    }
    
    static {
        LOCALE = Locale.US;
        logger = Logger.getLogger(CellFormatter.class.getName());
    }
}
