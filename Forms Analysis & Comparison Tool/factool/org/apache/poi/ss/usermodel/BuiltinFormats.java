// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;

public final class BuiltinFormats
{
    public static final int FIRST_USER_DEFINED_FORMAT_INDEX = 164;
    private static final String[] _formats;
    
    private static void putFormat(final List<String> m, final int index, final String value) {
        if (m.size() != index) {
            throw new IllegalStateException("index " + index + " is wrong");
        }
        m.add(value);
    }
    
    @Deprecated
    public static Map<Integer, String> getBuiltinFormats() {
        final Map<Integer, String> result = new LinkedHashMap<Integer, String>();
        for (int i = 0; i < BuiltinFormats._formats.length; ++i) {
            result.put(i, BuiltinFormats._formats[i]);
        }
        return result;
    }
    
    public static String[] getAll() {
        return BuiltinFormats._formats.clone();
    }
    
    public static String getBuiltinFormat(final int index) {
        if (index < 0 || index >= BuiltinFormats._formats.length) {
            return null;
        }
        return BuiltinFormats._formats[index];
    }
    
    public static int getBuiltinFormat(final String pFmt) {
        String fmt;
        if (pFmt.equalsIgnoreCase("TEXT")) {
            fmt = "@";
        }
        else {
            fmt = pFmt;
        }
        for (int i = 0; i < BuiltinFormats._formats.length; ++i) {
            if (fmt.equals(BuiltinFormats._formats[i])) {
                return i;
            }
        }
        return -1;
    }
    
    static {
        final List<String> m = new ArrayList<String>();
        putFormat(m, 0, "General");
        putFormat(m, 1, "0");
        putFormat(m, 2, "0.00");
        putFormat(m, 3, "#,##0");
        putFormat(m, 4, "#,##0.00");
        putFormat(m, 5, "\"$\"#,##0_);(\"$\"#,##0)");
        putFormat(m, 6, "\"$\"#,##0_);[Red](\"$\"#,##0)");
        putFormat(m, 7, "\"$\"#,##0.00_);(\"$\"#,##0.00)");
        putFormat(m, 8, "\"$\"#,##0.00_);[Red](\"$\"#,##0.00)");
        putFormat(m, 9, "0%");
        putFormat(m, 10, "0.00%");
        putFormat(m, 11, "0.00E+00");
        putFormat(m, 12, "# ?/?");
        putFormat(m, 13, "# ??/??");
        putFormat(m, 14, "m/d/yy");
        putFormat(m, 15, "d-mmm-yy");
        putFormat(m, 16, "d-mmm");
        putFormat(m, 17, "mmm-yy");
        putFormat(m, 18, "h:mm AM/PM");
        putFormat(m, 19, "h:mm:ss AM/PM");
        putFormat(m, 20, "h:mm");
        putFormat(m, 21, "h:mm:ss");
        putFormat(m, 22, "m/d/yy h:mm");
        for (int i = 23; i <= 36; ++i) {
            putFormat(m, i, "reserved-0x" + Integer.toHexString(i));
        }
        putFormat(m, 37, "#,##0_);(#,##0)");
        putFormat(m, 38, "#,##0_);[Red](#,##0)");
        putFormat(m, 39, "#,##0.00_);(#,##0.00)");
        putFormat(m, 40, "#,##0.00_);[Red](#,##0.00)");
        putFormat(m, 41, "_(\"$\"* #,##0_);_(\"$\"* (#,##0);_(\"$\"* \"-\"_);_(@_)");
        putFormat(m, 42, "_(* #,##0_);_(* (#,##0);_(* \"-\"_);_(@_)");
        putFormat(m, 43, "_(* #,##0.00_);_(* (#,##0.00);_(* \"-\"??_);_(@_)");
        putFormat(m, 44, "_(\"$\"* #,##0.00_);_(\"$\"* (#,##0.00);_(\"$\"* \"-\"??_);_(@_)");
        putFormat(m, 45, "mm:ss");
        putFormat(m, 46, "[h]:mm:ss");
        putFormat(m, 47, "mm:ss.0");
        putFormat(m, 48, "##0.0E+0");
        putFormat(m, 49, "@");
        final String[] ss = new String[m.size()];
        m.toArray(ss);
        _formats = ss;
    }
}
