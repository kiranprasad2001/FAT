// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

import java.text.ParsePosition;
import java.text.FieldPosition;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.HashMap;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Map;
import java.text.Format;
import java.text.DateFormatSymbols;
import java.text.DecimalFormatSymbols;
import java.util.regex.Pattern;

public class DataFormatter
{
    private static final String defaultFractionWholePartFormat = "#";
    private static final String defaultFractionFractionPartFormat = "#/##";
    private static final Pattern numPattern;
    private static final Pattern daysAsText;
    private static final Pattern amPmPattern;
    private static final Pattern localePatternGroup;
    private static final Pattern colorPattern;
    private static final Pattern fractionPattern;
    private static final Pattern fractionStripper;
    private static final String invalidDateTimeString;
    private final DecimalFormatSymbols decimalSymbols;
    private final DateFormatSymbols dateSymbols;
    private final Format generalWholeNumFormat;
    private final Format generalDecimalNumFormat;
    private Format defaultNumFormat;
    private final Map<String, Format> formats;
    private boolean emulateCsv;
    
    public DataFormatter() {
        this(false);
    }
    
    public DataFormatter(final boolean emulateCsv) {
        this(Locale.getDefault());
        this.emulateCsv = emulateCsv;
    }
    
    public DataFormatter(final Locale locale, final boolean emulateCsv) {
        this(locale);
        this.emulateCsv = emulateCsv;
    }
    
    public DataFormatter(final Locale locale) {
        this.emulateCsv = false;
        this.dateSymbols = new DateFormatSymbols(locale);
        this.decimalSymbols = new DecimalFormatSymbols(locale);
        this.generalWholeNumFormat = new DecimalFormat("#", this.decimalSymbols);
        this.generalDecimalNumFormat = new DecimalFormat("#.##########", this.decimalSymbols);
        this.formats = new HashMap<String, Format>();
        final Format zipFormat = ZipPlusFourFormat.instance;
        this.addFormat("00000\\-0000", zipFormat);
        this.addFormat("00000-0000", zipFormat);
        final Format phoneFormat = PhoneFormat.instance;
        this.addFormat("[<=9999999]###\\-####;\\(###\\)\\ ###\\-####", phoneFormat);
        this.addFormat("[<=9999999]###-####;(###) ###-####", phoneFormat);
        this.addFormat("###\\-####;\\(###\\)\\ ###\\-####", phoneFormat);
        this.addFormat("###-####;(###) ###-####", phoneFormat);
        final Format ssnFormat = SSNFormat.instance;
        this.addFormat("000\\-00\\-0000", ssnFormat);
        this.addFormat("000-00-0000", ssnFormat);
    }
    
    private Format getFormat(final Cell cell) {
        if (cell.getCellStyle() == null) {
            return null;
        }
        final int formatIndex = cell.getCellStyle().getDataFormat();
        final String formatStr = cell.getCellStyle().getDataFormatString();
        if (formatStr == null || formatStr.trim().length() == 0) {
            return null;
        }
        return this.getFormat(cell.getNumericCellValue(), formatIndex, formatStr);
    }
    
    private Format getFormat(final double cellValue, final int formatIndex, final String formatStrIn) {
        String formatStr = formatStrIn;
        final int firstAt = formatStr.indexOf(59);
        final int lastAt = formatStr.lastIndexOf(59);
        if (firstAt != -1 && firstAt != lastAt) {
            final int secondAt = formatStr.indexOf(59, firstAt + 1);
            if (secondAt == lastAt) {
                if (cellValue == 0.0) {
                    formatStr = formatStr.substring(lastAt + 1);
                }
                else {
                    formatStr = formatStr.substring(0, lastAt);
                }
            }
            else if (cellValue == 0.0) {
                formatStr = formatStr.substring(secondAt + 1, lastAt);
            }
            else {
                formatStr = formatStr.substring(0, secondAt);
            }
        }
        if (this.emulateCsv && cellValue == 0.0 && formatStr.contains("#") && !formatStr.contains("0")) {
            formatStr = formatStr.replaceAll("#", "");
        }
        Format format = this.formats.get(formatStr);
        if (format != null) {
            return format;
        }
        if (!"General".equalsIgnoreCase(formatStr) && !"@".equals(formatStr)) {
            format = this.createFormat(cellValue, formatIndex, formatStr);
            this.formats.put(formatStr, format);
            return format;
        }
        if (isWholeNumber(cellValue)) {
            return this.generalWholeNumFormat;
        }
        return this.generalDecimalNumFormat;
    }
    
    public Format createFormat(final Cell cell) {
        final int formatIndex = cell.getCellStyle().getDataFormat();
        final String formatStr = cell.getCellStyle().getDataFormatString();
        return this.createFormat(cell.getNumericCellValue(), formatIndex, formatStr);
    }
    
    private Format createFormat(final double cellValue, final int formatIndex, final String sFormat) {
        String formatStr = sFormat;
        for (Matcher colourM = DataFormatter.colorPattern.matcher(formatStr); colourM.find(); colourM = DataFormatter.colorPattern.matcher(formatStr)) {
            final String colour = colourM.group();
            final int at = formatStr.indexOf(colour);
            if (at == -1) {
                break;
            }
            final String nFormatStr = formatStr.substring(0, at) + formatStr.substring(at + colour.length());
            if (nFormatStr.equals(formatStr)) {
                break;
            }
            formatStr = nFormatStr;
        }
        for (Matcher m = DataFormatter.localePatternGroup.matcher(formatStr); m.find(); m = DataFormatter.localePatternGroup.matcher(formatStr)) {
            final String match = m.group();
            String symbol = match.substring(match.indexOf(36) + 1, match.indexOf(45));
            if (symbol.indexOf(36) > -1) {
                final StringBuffer sb = new StringBuffer();
                sb.append(symbol.substring(0, symbol.indexOf(36)));
                sb.append('\\');
                sb.append(symbol.substring(symbol.indexOf(36), symbol.length()));
                symbol = sb.toString();
            }
            formatStr = m.replaceAll(symbol);
        }
        if (formatStr == null || formatStr.trim().length() == 0) {
            return this.getDefaultFormat(cellValue);
        }
        if ("General".equalsIgnoreCase(formatStr) || "@".equals(formatStr)) {
            if (isWholeNumber(cellValue)) {
                return this.generalWholeNumFormat;
            }
            return this.generalDecimalNumFormat;
        }
        else {
            if (DateUtil.isADateFormat(formatIndex, formatStr) && DateUtil.isValidExcelDate(cellValue)) {
                return this.createDateFormat(formatStr, cellValue);
            }
            if (formatStr.indexOf("#/") >= 0 || formatStr.indexOf("?/") >= 0) {
                final String[] chunks = formatStr.split(";");
                for (int i = 0; i < chunks.length; ++i) {
                    String chunk = chunks[i].replaceAll("\\?", "#");
                    final Matcher matcher = DataFormatter.fractionStripper.matcher(chunk);
                    chunk = matcher.replaceAll(" ");
                    chunk = chunk.replaceAll(" +", " ");
                    final Matcher fractionMatcher = DataFormatter.fractionPattern.matcher(chunk);
                    if (fractionMatcher.find()) {
                        final String wholePart = (fractionMatcher.group(1) == null) ? "" : "#";
                        return new FractionFormat(wholePart, fractionMatcher.group(3));
                    }
                }
                return new FractionFormat("#", "#/##");
            }
            if (DataFormatter.numPattern.matcher(formatStr).find()) {
                return this.createNumberFormat(formatStr, cellValue);
            }
            if (this.emulateCsv) {
                return new ConstantStringFormat(this.cleanFormatForNumber(formatStr));
            }
            return null;
        }
    }
    
    private Format createDateFormat(final String pFormatStr, final double cellValue) {
        String formatStr = pFormatStr;
        formatStr = formatStr.replaceAll("\\\\-", "-");
        formatStr = formatStr.replaceAll("\\\\,", ",");
        formatStr = formatStr.replaceAll("\\\\\\.", ".");
        formatStr = formatStr.replaceAll("\\\\ ", " ");
        formatStr = formatStr.replaceAll("\\\\/", "/");
        formatStr = formatStr.replaceAll(";@", "");
        formatStr = formatStr.replaceAll("\"/\"", "/");
        formatStr = formatStr.replace("\"\"", "'");
        formatStr = formatStr.replaceAll("\\\\T", "'T'");
        boolean hasAmPm = false;
        for (Matcher amPmMatcher = DataFormatter.amPmPattern.matcher(formatStr); amPmMatcher.find(); amPmMatcher = DataFormatter.amPmPattern.matcher(formatStr)) {
            formatStr = amPmMatcher.replaceAll("@");
            hasAmPm = true;
        }
        formatStr = formatStr.replaceAll("@", "a");
        final Matcher dateMatcher = DataFormatter.daysAsText.matcher(formatStr);
        if (dateMatcher.find()) {
            final String match = dateMatcher.group(0);
            formatStr = dateMatcher.replaceAll(match.toUpperCase().replaceAll("D", "E"));
        }
        final StringBuffer sb = new StringBuffer();
        final char[] chars = formatStr.toCharArray();
        boolean mIsMonth = true;
        final List<Integer> ms = new ArrayList<Integer>();
        boolean isElapsed = false;
        for (int j = 0; j < chars.length; ++j) {
            char c = chars[j];
            if (c == '\'') {
                sb.append(c);
                ++j;
                while (j < chars.length) {
                    c = chars[j];
                    sb.append(c);
                    if (c == '\'') {
                        break;
                    }
                    ++j;
                }
            }
            else if (c == '[' && !isElapsed) {
                isElapsed = true;
                mIsMonth = false;
                sb.append(c);
            }
            else if (c == ']' && isElapsed) {
                isElapsed = false;
                sb.append(c);
            }
            else if (isElapsed) {
                if (c == 'h' || c == 'H') {
                    sb.append('H');
                }
                else if (c == 'm' || c == 'M') {
                    sb.append('m');
                }
                else if (c == 's' || c == 'S') {
                    sb.append('s');
                }
                else {
                    sb.append(c);
                }
            }
            else if (c == 'h' || c == 'H') {
                mIsMonth = false;
                if (hasAmPm) {
                    sb.append('h');
                }
                else {
                    sb.append('H');
                }
            }
            else if (c == 'm' || c == 'M') {
                if (mIsMonth) {
                    sb.append('M');
                    ms.add(sb.length() - 1);
                }
                else {
                    sb.append('m');
                }
            }
            else if (c == 's' || c == 'S') {
                sb.append('s');
                for (int i = 0; i < ms.size(); ++i) {
                    final int index = ms.get(i);
                    if (sb.charAt(index) == 'M') {
                        sb.replace(index, index + 1, "m");
                    }
                }
                mIsMonth = true;
                ms.clear();
            }
            else if (Character.isLetter(c)) {
                mIsMonth = true;
                ms.clear();
                if (c == 'y' || c == 'Y') {
                    sb.append('y');
                }
                else if (c == 'd' || c == 'D') {
                    sb.append('d');
                }
                else {
                    sb.append(c);
                }
            }
            else {
                sb.append(c);
            }
        }
        formatStr = sb.toString();
        try {
            return new ExcelStyleDateFormatter(formatStr, this.dateSymbols);
        }
        catch (IllegalArgumentException iae) {
            return this.getDefaultFormat(cellValue);
        }
    }
    
    private String cleanFormatForNumber(final String formatStr) {
        final StringBuffer sb = new StringBuffer(formatStr);
        if (this.emulateCsv) {
            for (int i = 0; i < sb.length(); ++i) {
                final char c = sb.charAt(i);
                if (c == '_' || c == '*' || c == '?') {
                    if (i <= 0 || sb.charAt(i - 1) != '\\') {
                        if (c == '?') {
                            sb.setCharAt(i, ' ');
                        }
                        else if (i < sb.length() - 1) {
                            if (c == '_') {
                                sb.setCharAt(i + 1, ' ');
                            }
                            else {
                                sb.deleteCharAt(i + 1);
                            }
                            sb.deleteCharAt(i);
                            --i;
                        }
                    }
                }
            }
        }
        else {
            for (int i = 0; i < sb.length(); ++i) {
                final char c = sb.charAt(i);
                if (c == '_' || c == '*') {
                    if (i <= 0 || sb.charAt(i - 1) != '\\') {
                        if (i < sb.length() - 1) {
                            sb.deleteCharAt(i + 1);
                        }
                        sb.deleteCharAt(i);
                        --i;
                    }
                }
            }
        }
        for (int i = 0; i < sb.length(); ++i) {
            final char c = sb.charAt(i);
            if (c == '\\' || c == '\"') {
                sb.deleteCharAt(i);
                --i;
            }
            else if (c == '+' && i > 0 && sb.charAt(i - 1) == 'E') {
                sb.deleteCharAt(i);
                --i;
            }
        }
        return sb.toString();
    }
    
    private Format createNumberFormat(final String formatStr, final double cellValue) {
        final String format = this.cleanFormatForNumber(formatStr);
        try {
            final DecimalFormat df = new DecimalFormat(format, this.decimalSymbols);
            setExcelStyleRoundingMode(df);
            return df;
        }
        catch (IllegalArgumentException iae) {
            return this.getDefaultFormat(cellValue);
        }
    }
    
    private static boolean isWholeNumber(final double d) {
        return d == Math.floor(d);
    }
    
    public Format getDefaultFormat(final Cell cell) {
        return this.getDefaultFormat(cell.getNumericCellValue());
    }
    
    private Format getDefaultFormat(final double cellValue) {
        if (this.defaultNumFormat != null) {
            return this.defaultNumFormat;
        }
        if (isWholeNumber(cellValue)) {
            return this.generalWholeNumFormat;
        }
        return this.generalDecimalNumFormat;
    }
    
    private String performDateFormatting(final Date d, final Format dateFormat) {
        if (dateFormat != null) {
            return dateFormat.format(d);
        }
        return d.toString();
    }
    
    private String getFormattedDateString(final Cell cell) {
        final Format dateFormat = this.getFormat(cell);
        if (dateFormat instanceof ExcelStyleDateFormatter) {
            ((ExcelStyleDateFormatter)dateFormat).setDateToBeFormatted(cell.getNumericCellValue());
        }
        final Date d = cell.getDateCellValue();
        return this.performDateFormatting(d, dateFormat);
    }
    
    private String getFormattedNumberString(final Cell cell) {
        final Format numberFormat = this.getFormat(cell);
        final double d = cell.getNumericCellValue();
        if (numberFormat == null) {
            return String.valueOf(d);
        }
        return numberFormat.format(new Double(d));
    }
    
    public String formatRawCellContents(final double value, final int formatIndex, final String formatString) {
        return this.formatRawCellContents(value, formatIndex, formatString, false);
    }
    
    public String formatRawCellContents(final double value, final int formatIndex, final String formatString, final boolean use1904Windowing) {
        if (DateUtil.isADateFormat(formatIndex, formatString)) {
            if (DateUtil.isValidExcelDate(value)) {
                final Format dateFormat = this.getFormat(value, formatIndex, formatString);
                if (dateFormat instanceof ExcelStyleDateFormatter) {
                    ((ExcelStyleDateFormatter)dateFormat).setDateToBeFormatted(value);
                }
                final Date d = DateUtil.getJavaDate(value, use1904Windowing);
                return this.performDateFormatting(d, dateFormat);
            }
            if (this.emulateCsv) {
                return DataFormatter.invalidDateTimeString;
            }
        }
        final Format numberFormat = this.getFormat(value, formatIndex, formatString);
        if (numberFormat == null) {
            return String.valueOf(value);
        }
        String result = numberFormat.format(new Double(value));
        if (result.contains("E") && !result.contains("E-")) {
            result = result.replaceFirst("E", "E+");
        }
        return result;
    }
    
    public String formatCellValue(final Cell cell) {
        return this.formatCellValue(cell, null);
    }
    
    public String formatCellValue(final Cell cell, final FormulaEvaluator evaluator) {
        if (cell == null) {
            return "";
        }
        int cellType = cell.getCellType();
        if (cellType == 2) {
            if (evaluator == null) {
                return cell.getCellFormula();
            }
            cellType = evaluator.evaluateFormulaCell(cell);
        }
        switch (cellType) {
            case 0: {
                if (DateUtil.isCellDateFormatted(cell)) {
                    return this.getFormattedDateString(cell);
                }
                return this.getFormattedNumberString(cell);
            }
            case 1: {
                return cell.getRichStringCellValue().getString();
            }
            case 4: {
                return String.valueOf(cell.getBooleanCellValue());
            }
            case 3: {
                return "";
            }
            case 5: {
                return FormulaError.forInt(cell.getErrorCellValue()).getString();
            }
            default: {
                throw new RuntimeException("Unexpected celltype (" + cellType + ")");
            }
        }
    }
    
    public void setDefaultNumberFormat(final Format format) {
        for (final Map.Entry<String, Format> entry : this.formats.entrySet()) {
            if (entry.getValue() == this.generalDecimalNumFormat || entry.getValue() == this.generalWholeNumFormat) {
                entry.setValue(format);
            }
        }
        this.defaultNumFormat = format;
    }
    
    public void addFormat(final String excelFormatStr, final Format format) {
        this.formats.put(excelFormatStr, format);
    }
    
    static DecimalFormat createIntegerOnlyFormat(final String fmt) {
        final DecimalFormat result = new DecimalFormat(fmt);
        result.setParseIntegerOnly(true);
        return result;
    }
    
    public static void setExcelStyleRoundingMode(final DecimalFormat format) {
        setExcelStyleRoundingMode(format, RoundingMode.HALF_UP);
    }
    
    public static void setExcelStyleRoundingMode(final DecimalFormat format, final RoundingMode roundingMode) {
        format.setRoundingMode(roundingMode);
    }
    
    static {
        numPattern = Pattern.compile("[0#]+");
        daysAsText = Pattern.compile("([d]{3,})", 2);
        amPmPattern = Pattern.compile("((A|P)[M/P]*)", 2);
        localePatternGroup = Pattern.compile("(\\[\\$[^-\\]]*-[0-9A-Z]+\\])");
        colorPattern = Pattern.compile("(\\[BLACK\\])|(\\[BLUE\\])|(\\[CYAN\\])|(\\[GREEN\\])|(\\[MAGENTA\\])|(\\[RED\\])|(\\[WHITE\\])|(\\[YELLOW\\])|(\\[COLOR\\s*\\d\\])|(\\[COLOR\\s*[0-5]\\d\\])", 2);
        fractionPattern = Pattern.compile("(?:([#\\d]+)\\s+)?(#+)\\s*\\/\\s*([#\\d]+)");
        fractionStripper = Pattern.compile("(\"[^\"]*\")|([^ \\?#\\d\\/]+)");
        final StringBuilder buf = new StringBuilder();
        for (int i = 0; i < 255; ++i) {
            buf.append('#');
        }
        invalidDateTimeString = buf.toString();
    }
    
    private static final class SSNFormat extends Format
    {
        public static final Format instance;
        private static final DecimalFormat df;
        
        public static String format(final Number num) {
            final String result = SSNFormat.df.format(num);
            final StringBuffer sb = new StringBuffer();
            sb.append(result.substring(0, 3)).append('-');
            sb.append(result.substring(3, 5)).append('-');
            sb.append(result.substring(5, 9));
            return sb.toString();
        }
        
        @Override
        public StringBuffer format(final Object obj, final StringBuffer toAppendTo, final FieldPosition pos) {
            return toAppendTo.append(format((Number)obj));
        }
        
        @Override
        public Object parseObject(final String source, final ParsePosition pos) {
            return SSNFormat.df.parseObject(source, pos);
        }
        
        static {
            instance = new SSNFormat();
            df = DataFormatter.createIntegerOnlyFormat("000000000");
        }
    }
    
    private static final class ZipPlusFourFormat extends Format
    {
        public static final Format instance;
        private static final DecimalFormat df;
        
        public static String format(final Number num) {
            final String result = ZipPlusFourFormat.df.format(num);
            final StringBuffer sb = new StringBuffer();
            sb.append(result.substring(0, 5)).append('-');
            sb.append(result.substring(5, 9));
            return sb.toString();
        }
        
        @Override
        public StringBuffer format(final Object obj, final StringBuffer toAppendTo, final FieldPosition pos) {
            return toAppendTo.append(format((Number)obj));
        }
        
        @Override
        public Object parseObject(final String source, final ParsePosition pos) {
            return ZipPlusFourFormat.df.parseObject(source, pos);
        }
        
        static {
            instance = new ZipPlusFourFormat();
            df = DataFormatter.createIntegerOnlyFormat("000000000");
        }
    }
    
    private static final class PhoneFormat extends Format
    {
        public static final Format instance;
        private static final DecimalFormat df;
        
        public static String format(final Number num) {
            final String result = PhoneFormat.df.format(num);
            final StringBuffer sb = new StringBuffer();
            final int len = result.length();
            if (len <= 4) {
                return result;
            }
            final String seg3 = result.substring(len - 4, len);
            final String seg4 = result.substring(Math.max(0, len - 7), len - 4);
            final String seg5 = result.substring(Math.max(0, len - 10), Math.max(0, len - 7));
            if (seg5 != null && seg5.trim().length() > 0) {
                sb.append('(').append(seg5).append(") ");
            }
            if (seg4 != null && seg4.trim().length() > 0) {
                sb.append(seg4).append('-');
            }
            sb.append(seg3);
            return sb.toString();
        }
        
        @Override
        public StringBuffer format(final Object obj, final StringBuffer toAppendTo, final FieldPosition pos) {
            return toAppendTo.append(format((Number)obj));
        }
        
        @Override
        public Object parseObject(final String source, final ParsePosition pos) {
            return PhoneFormat.df.parseObject(source, pos);
        }
        
        static {
            instance = new PhoneFormat();
            df = DataFormatter.createIntegerOnlyFormat("##########");
        }
    }
    
    private static final class ConstantStringFormat extends Format
    {
        private static final DecimalFormat df;
        private final String str;
        
        public ConstantStringFormat(final String s) {
            this.str = s;
        }
        
        @Override
        public StringBuffer format(final Object obj, final StringBuffer toAppendTo, final FieldPosition pos) {
            return toAppendTo.append(this.str);
        }
        
        @Override
        public Object parseObject(final String source, final ParsePosition pos) {
            return ConstantStringFormat.df.parseObject(source, pos);
        }
        
        static {
            df = DataFormatter.createIntegerOnlyFormat("##########");
        }
    }
}
