// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class DateUtil
{
    public static final int SECONDS_PER_MINUTE = 60;
    public static final int MINUTES_PER_HOUR = 60;
    public static final int HOURS_PER_DAY = 24;
    public static final int SECONDS_PER_DAY = 86400;
    private static final int BAD_DATE = -1;
    public static final long DAY_MILLISECONDS = 86400000L;
    private static final Pattern TIME_SEPARATOR_PATTERN;
    private static final Pattern date_ptrn1;
    private static final Pattern date_ptrn2;
    private static final Pattern date_ptrn3a;
    private static final Pattern date_ptrn3b;
    private static final Pattern date_ptrn4;
    private static final TimeZone TIMEZONE_UTC;
    private static ThreadLocal<Integer> lastFormatIndex;
    private static ThreadLocal<String> lastFormatString;
    private static ThreadLocal<Boolean> lastCachedResult;
    
    protected DateUtil() {
    }
    
    public static double getExcelDate(final Date date) {
        return getExcelDate(date, false);
    }
    
    public static double getExcelDate(final Date date, final boolean use1904windowing) {
        final Calendar calStart = new GregorianCalendar();
        calStart.setTime(date);
        return internalGetExcelDate(calStart, use1904windowing);
    }
    
    public static double getExcelDate(final Calendar date, final boolean use1904windowing) {
        return internalGetExcelDate((Calendar)date.clone(), use1904windowing);
    }
    
    private static double internalGetExcelDate(final Calendar date, final boolean use1904windowing) {
        if ((!use1904windowing && date.get(1) < 1900) || (use1904windowing && date.get(1) < 1904)) {
            return -1.0;
        }
        final double fraction = (((date.get(11) * 60 + date.get(12)) * 60 + date.get(13)) * 1000 + date.get(14)) / 8.64E7;
        final Calendar calStart = dayStart(date);
        double value = fraction + absoluteDay(calStart, use1904windowing);
        if (!use1904windowing && value >= 60.0) {
            ++value;
        }
        else if (use1904windowing) {
            --value;
        }
        return value;
    }
    
    public static Date getJavaDate(final double date, final TimeZone tz) {
        return getJavaDate(date, false, tz);
    }
    
    public static Date getJavaDate(final double date) {
        return getJavaDate(date, null);
    }
    
    public static Date getJavaDate(final double date, final boolean use1904windowing, final TimeZone tz) {
        return getJavaCalendar(date, use1904windowing, tz, false).getTime();
    }
    
    public static Date getJavaDate(final double date, final boolean use1904windowing, final TimeZone tz, final boolean roundSeconds) {
        return getJavaCalendar(date, use1904windowing, tz, roundSeconds).getTime();
    }
    
    public static Date getJavaDate(final double date, final boolean use1904windowing) {
        return getJavaCalendar(date, use1904windowing, null, false).getTime();
    }
    
    public static void setCalendar(final Calendar calendar, final int wholeDays, final int millisecondsInDay, final boolean use1904windowing, final boolean roundSeconds) {
        int startYear = 1900;
        int dayAdjust = -1;
        if (use1904windowing) {
            startYear = 1904;
            dayAdjust = 1;
        }
        else if (wholeDays < 61) {
            dayAdjust = 0;
        }
        calendar.set(startYear, 0, wholeDays + dayAdjust, 0, 0, 0);
        calendar.set(14, millisecondsInDay);
        if (roundSeconds) {
            calendar.add(14, 500);
            calendar.clear(14);
        }
    }
    
    public static Calendar getJavaCalendar(final double date) {
        return getJavaCalendar(date, false, null, false);
    }
    
    public static Calendar getJavaCalendar(final double date, final boolean use1904windowing) {
        return getJavaCalendar(date, use1904windowing, null, false);
    }
    
    public static Calendar getJavaCalendarUTC(final double date, final boolean use1904windowing) {
        return getJavaCalendar(date, use1904windowing, DateUtil.TIMEZONE_UTC, false);
    }
    
    public static Calendar getJavaCalendar(final double date, final boolean use1904windowing, final TimeZone timeZone) {
        return getJavaCalendar(date, use1904windowing, timeZone, false);
    }
    
    public static Calendar getJavaCalendar(final double date, final boolean use1904windowing, final TimeZone timeZone, final boolean roundSeconds) {
        if (!isValidExcelDate(date)) {
            return null;
        }
        final int wholeDays = (int)Math.floor(date);
        final int millisecondsInDay = (int)((date - wholeDays) * 8.64E7 + 0.5);
        Calendar calendar;
        if (timeZone != null) {
            calendar = new GregorianCalendar(timeZone);
        }
        else {
            calendar = new GregorianCalendar();
        }
        setCalendar(calendar, wholeDays, millisecondsInDay, use1904windowing, roundSeconds);
        return calendar;
    }
    
    private static boolean isCached(final String formatString, final int formatIndex) {
        final String cachedFormatString = DateUtil.lastFormatString.get();
        return cachedFormatString != null && formatIndex == DateUtil.lastFormatIndex.get() && formatString.equals(cachedFormatString);
    }
    
    private static void cache(final String formatString, final int formatIndex, final boolean cached) {
        DateUtil.lastFormatIndex.set(formatIndex);
        DateUtil.lastFormatString.set(formatString);
        DateUtil.lastCachedResult.set(cached);
    }
    
    public static boolean isADateFormat(final int formatIndex, final String formatString) {
        if (isInternalDateFormat(formatIndex)) {
            cache(formatString, formatIndex, true);
            return true;
        }
        if (formatString == null || formatString.length() == 0) {
            return false;
        }
        if (isCached(formatString, formatIndex)) {
            return DateUtil.lastCachedResult.get();
        }
        String fs = formatString;
        final StringBuilder sb = new StringBuilder(fs.length());
        for (int i = 0; i < fs.length(); ++i) {
            final char c = fs.charAt(i);
            if (i < fs.length() - 1) {
                final char nc = fs.charAt(i + 1);
                if (c == '\\') {
                    switch (nc) {
                        case ' ':
                        case ',':
                        case '-':
                        case '.':
                        case '\\': {
                            continue;
                        }
                    }
                }
                else if (c == ';' && nc == '@') {
                    ++i;
                    continue;
                }
            }
            sb.append(c);
        }
        fs = sb.toString();
        if (DateUtil.date_ptrn4.matcher(fs).matches()) {
            cache(formatString, formatIndex, true);
            return true;
        }
        fs = DateUtil.date_ptrn1.matcher(fs).replaceAll("");
        fs = DateUtil.date_ptrn2.matcher(fs).replaceAll("");
        if (fs.indexOf(59) > 0 && fs.indexOf(59) < fs.length() - 1) {
            fs = fs.substring(0, fs.indexOf(59));
        }
        if (!DateUtil.date_ptrn3a.matcher(fs).find()) {
            return false;
        }
        final boolean result = DateUtil.date_ptrn3b.matcher(fs).matches();
        cache(formatString, formatIndex, result);
        return result;
    }
    
    public static boolean isInternalDateFormat(final int format) {
        switch (format) {
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 45:
            case 46:
            case 47: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static boolean isCellDateFormatted(final Cell cell) {
        if (cell == null) {
            return false;
        }
        boolean bDate = false;
        final double d = cell.getNumericCellValue();
        if (isValidExcelDate(d)) {
            final CellStyle style = cell.getCellStyle();
            if (style == null) {
                return false;
            }
            final int i = style.getDataFormat();
            final String f = style.getDataFormatString();
            bDate = isADateFormat(i, f);
        }
        return bDate;
    }
    
    public static boolean isCellInternalDateFormatted(final Cell cell) {
        if (cell == null) {
            return false;
        }
        boolean bDate = false;
        final double d = cell.getNumericCellValue();
        if (isValidExcelDate(d)) {
            final CellStyle style = cell.getCellStyle();
            final int i = style.getDataFormat();
            bDate = isInternalDateFormat(i);
        }
        return bDate;
    }
    
    public static boolean isValidExcelDate(final double value) {
        return value > -4.9E-324;
    }
    
    protected static int absoluteDay(final Calendar cal, final boolean use1904windowing) {
        return cal.get(6) + daysInPriorYears(cal.get(1), use1904windowing);
    }
    
    private static int daysInPriorYears(final int yr, final boolean use1904windowing) {
        if ((!use1904windowing && yr < 1900) || (use1904windowing && yr < 1900)) {
            throw new IllegalArgumentException("'year' must be 1900 or greater");
        }
        final int yr2 = yr - 1;
        final int leapDays = yr2 / 4 - yr2 / 100 + yr2 / 400 - 460;
        return 365 * (yr - (use1904windowing ? 1904 : 1900)) + leapDays;
    }
    
    private static Calendar dayStart(final Calendar cal) {
        cal.get(11);
        cal.set(11, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(14, 0);
        cal.get(11);
        return cal;
    }
    
    public static double convertTime(final String timeStr) {
        try {
            return convertTimeInternal(timeStr);
        }
        catch (FormatException e) {
            final String msg = "Bad time format '" + timeStr + "' expected 'HH:MM' or 'HH:MM:SS' - " + e.getMessage();
            throw new IllegalArgumentException(msg);
        }
    }
    
    private static double convertTimeInternal(final String timeStr) throws FormatException {
        final int len = timeStr.length();
        if (len < 4 || len > 8) {
            throw new FormatException("Bad length");
        }
        final String[] parts = DateUtil.TIME_SEPARATOR_PATTERN.split(timeStr);
        String secStr = null;
        switch (parts.length) {
            case 2: {
                secStr = "00";
                break;
            }
            case 3: {
                secStr = parts[2];
                break;
            }
            default: {
                throw new FormatException("Expected 2 or 3 fields but got (" + parts.length + ")");
            }
        }
        final String hourStr = parts[0];
        final String minStr = parts[1];
        final int hours = parseInt(hourStr, "hour", 24);
        final int minutes = parseInt(minStr, "minute", 60);
        final int seconds = parseInt(secStr, "second", 60);
        final double totalSeconds = seconds + (minutes + hours * 60) * 60;
        return totalSeconds / 86400.0;
    }
    
    public static Date parseYYYYMMDDDate(final String dateStr) {
        try {
            return parseYYYYMMDDDateInternal(dateStr);
        }
        catch (FormatException e) {
            final String msg = "Bad time format " + dateStr + " expected 'YYYY/MM/DD' - " + e.getMessage();
            throw new IllegalArgumentException(msg);
        }
    }
    
    private static Date parseYYYYMMDDDateInternal(final String timeStr) throws FormatException {
        if (timeStr.length() != 10) {
            throw new FormatException("Bad length");
        }
        final String yearStr = timeStr.substring(0, 4);
        final String monthStr = timeStr.substring(5, 7);
        final String dayStr = timeStr.substring(8, 10);
        final int year = parseInt(yearStr, "year", -32768, 32767);
        final int month = parseInt(monthStr, "month", 1, 12);
        final int day = parseInt(dayStr, "day", 1, 31);
        final Calendar cal = new GregorianCalendar(year, month - 1, day, 0, 0, 0);
        cal.set(14, 0);
        return cal.getTime();
    }
    
    private static int parseInt(final String strVal, final String fieldName, final int rangeMax) throws FormatException {
        return parseInt(strVal, fieldName, 0, rangeMax - 1);
    }
    
    private static int parseInt(final String strVal, final String fieldName, final int lowerLimit, final int upperLimit) throws FormatException {
        int result;
        try {
            result = Integer.parseInt(strVal);
        }
        catch (NumberFormatException e) {
            throw new FormatException("Bad int format '" + strVal + "' for " + fieldName + " field");
        }
        if (result < lowerLimit || result > upperLimit) {
            throw new FormatException(fieldName + " value (" + result + ") is outside the allowable range(0.." + upperLimit + ")");
        }
        return result;
    }
    
    static {
        TIME_SEPARATOR_PATTERN = Pattern.compile(":");
        date_ptrn1 = Pattern.compile("^\\[\\$\\-.*?\\]");
        date_ptrn2 = Pattern.compile("^\\[[a-zA-Z]+\\]");
        date_ptrn3a = Pattern.compile("[yYmMdDhHsS]");
        date_ptrn3b = Pattern.compile("^[\\[\\]yYmMdDhHsS\\-T/,. :\"\\\\]+0*[ampAMP/]*$");
        date_ptrn4 = Pattern.compile("^\\[([hH]+|[mM]+|[sS]+)\\]");
        TIMEZONE_UTC = TimeZone.getTimeZone("UTC");
        DateUtil.lastFormatIndex = new ThreadLocal<Integer>() {
            @Override
            protected Integer initialValue() {
                return -1;
            }
        };
        DateUtil.lastFormatString = new ThreadLocal<String>();
        DateUtil.lastCachedResult = new ThreadLocal<Boolean>();
    }
    
    private static final class FormatException extends Exception
    {
        public FormatException(final String msg) {
            super(msg);
        }
    }
}
