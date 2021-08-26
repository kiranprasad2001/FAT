// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.format;

import java.util.Formatter;

public class CellGeneralFormatter extends CellFormatter
{
    public CellGeneralFormatter() {
        super("General");
    }
    
    @Override
    public void formatValue(final StringBuffer toAppendTo, final Object value) {
        if (value instanceof Number) {
            final double val = ((Number)value).doubleValue();
            if (val == 0.0) {
                toAppendTo.append('0');
                return;
            }
            final double exp = Math.log10(Math.abs(val));
            boolean stripZeros = true;
            String fmt;
            if (exp > 10.0 || exp < -9.0) {
                fmt = "%1.5E";
            }
            else if ((long)val != val) {
                fmt = "%1.9f";
            }
            else {
                fmt = "%1.0f";
                stripZeros = false;
            }
            final Formatter formatter = new Formatter(toAppendTo);
            try {
                formatter.format(CellGeneralFormatter.LOCALE, fmt, value);
            }
            finally {
                formatter.close();
            }
            if (stripZeros) {
                int removeFrom;
                if (fmt.endsWith("E")) {
                    removeFrom = toAppendTo.lastIndexOf("E") - 1;
                }
                else {
                    removeFrom = toAppendTo.length() - 1;
                }
                while (toAppendTo.charAt(removeFrom) == '0') {
                    toAppendTo.deleteCharAt(removeFrom--);
                }
                if (toAppendTo.charAt(removeFrom) == '.') {
                    toAppendTo.deleteCharAt(removeFrom--);
                }
            }
        }
        else if (value instanceof Boolean) {
            toAppendTo.append(value.toString().toUpperCase());
        }
        else {
            toAppendTo.append(value.toString());
        }
    }
    
    @Override
    public void simpleValue(final StringBuffer toAppendTo, final Object value) {
        this.formatValue(toAppendTo, value);
    }
}
