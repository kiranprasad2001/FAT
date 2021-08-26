// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import java.util.Calendar;
import org.apache.poi.ss.usermodel.DateUtil;

public class HSSFDateUtil extends DateUtil
{
    protected static int absoluteDay(final Calendar cal, final boolean use1904windowing) {
        return DateUtil.absoluteDay(cal, use1904windowing);
    }
}
