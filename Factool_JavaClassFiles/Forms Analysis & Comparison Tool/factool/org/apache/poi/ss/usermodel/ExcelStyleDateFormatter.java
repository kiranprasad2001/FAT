// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

import java.text.FieldPosition;
import java.util.Date;
import java.util.Locale;
import java.text.DateFormatSymbols;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class ExcelStyleDateFormatter extends SimpleDateFormat
{
    public static final char MMMMM_START_SYMBOL = '\ue001';
    public static final char MMMMM_TRUNCATE_SYMBOL = '\ue002';
    public static final char H_BRACKET_SYMBOL = '\ue010';
    public static final char HH_BRACKET_SYMBOL = '\ue011';
    public static final char M_BRACKET_SYMBOL = '\ue012';
    public static final char MM_BRACKET_SYMBOL = '\ue013';
    public static final char S_BRACKET_SYMBOL = '\ue014';
    public static final char SS_BRACKET_SYMBOL = '\ue015';
    public static final char L_BRACKET_SYMBOL = '\ue016';
    public static final char LL_BRACKET_SYMBOL = '\ue017';
    private DecimalFormat format1digit;
    private DecimalFormat format2digits;
    private DecimalFormat format3digit;
    private DecimalFormat format4digits;
    private double dateToBeFormatted;
    
    public ExcelStyleDateFormatter() {
        this.format1digit = new DecimalFormat("0");
        this.format2digits = new DecimalFormat("00");
        this.format3digit = new DecimalFormat("0");
        this.format4digits = new DecimalFormat("00");
        DataFormatter.setExcelStyleRoundingMode(this.format1digit, RoundingMode.DOWN);
        DataFormatter.setExcelStyleRoundingMode(this.format2digits, RoundingMode.DOWN);
        DataFormatter.setExcelStyleRoundingMode(this.format3digit);
        DataFormatter.setExcelStyleRoundingMode(this.format4digits);
        this.dateToBeFormatted = 0.0;
    }
    
    public ExcelStyleDateFormatter(final String pattern) {
        super(processFormatPattern(pattern));
        this.format1digit = new DecimalFormat("0");
        this.format2digits = new DecimalFormat("00");
        this.format3digit = new DecimalFormat("0");
        this.format4digits = new DecimalFormat("00");
        DataFormatter.setExcelStyleRoundingMode(this.format1digit, RoundingMode.DOWN);
        DataFormatter.setExcelStyleRoundingMode(this.format2digits, RoundingMode.DOWN);
        DataFormatter.setExcelStyleRoundingMode(this.format3digit);
        DataFormatter.setExcelStyleRoundingMode(this.format4digits);
        this.dateToBeFormatted = 0.0;
    }
    
    public ExcelStyleDateFormatter(final String pattern, final DateFormatSymbols formatSymbols) {
        super(processFormatPattern(pattern), formatSymbols);
        this.format1digit = new DecimalFormat("0");
        this.format2digits = new DecimalFormat("00");
        this.format3digit = new DecimalFormat("0");
        this.format4digits = new DecimalFormat("00");
        DataFormatter.setExcelStyleRoundingMode(this.format1digit, RoundingMode.DOWN);
        DataFormatter.setExcelStyleRoundingMode(this.format2digits, RoundingMode.DOWN);
        DataFormatter.setExcelStyleRoundingMode(this.format3digit);
        DataFormatter.setExcelStyleRoundingMode(this.format4digits);
        this.dateToBeFormatted = 0.0;
    }
    
    public ExcelStyleDateFormatter(final String pattern, final Locale locale) {
        super(processFormatPattern(pattern), locale);
        this.format1digit = new DecimalFormat("0");
        this.format2digits = new DecimalFormat("00");
        this.format3digit = new DecimalFormat("0");
        this.format4digits = new DecimalFormat("00");
        DataFormatter.setExcelStyleRoundingMode(this.format1digit, RoundingMode.DOWN);
        DataFormatter.setExcelStyleRoundingMode(this.format2digits, RoundingMode.DOWN);
        DataFormatter.setExcelStyleRoundingMode(this.format3digit);
        DataFormatter.setExcelStyleRoundingMode(this.format4digits);
        this.dateToBeFormatted = 0.0;
    }
    
    private static String processFormatPattern(final String f) {
        String t = f.replaceAll("MMMMM", "\ue001MMM\ue002");
        t = t.replaceAll("\\[H\\]", String.valueOf('\ue010'));
        t = t.replaceAll("\\[HH\\]", String.valueOf('\ue011'));
        t = t.replaceAll("\\[m\\]", String.valueOf('\ue012'));
        t = t.replaceAll("\\[mm\\]", String.valueOf('\ue013'));
        t = t.replaceAll("\\[s\\]", String.valueOf('\ue014'));
        t = t.replaceAll("\\[ss\\]", String.valueOf('\ue015'));
        t = t.replaceAll("s.000", "s.SSS");
        t = t.replaceAll("s.00", "s.\ue017");
        t = t.replaceAll("s.0", "s.\ue016");
        return t;
    }
    
    public void setDateToBeFormatted(final double date) {
        this.dateToBeFormatted = date;
    }
    
    @Override
    public StringBuffer format(final Date date, final StringBuffer paramStringBuffer, final FieldPosition paramFieldPosition) {
        String s = super.format(date, paramStringBuffer, paramFieldPosition).toString();
        if (s.indexOf(57345) != -1) {
            s = s.replaceAll("\ue001(\\w)\\w+\ue002", "$1");
        }
        if (s.indexOf(57360) != -1 || s.indexOf(57361) != -1) {
            final float hours = (float)this.dateToBeFormatted * 24.0f;
            s = s.replaceAll(String.valueOf('\ue010'), this.format1digit.format(hours));
            s = s.replaceAll(String.valueOf('\ue011'), this.format2digits.format(hours));
        }
        if (s.indexOf(57362) != -1 || s.indexOf(57363) != -1) {
            final float minutes = (float)this.dateToBeFormatted * 24.0f * 60.0f;
            s = s.replaceAll(String.valueOf('\ue012'), this.format1digit.format(minutes));
            s = s.replaceAll(String.valueOf('\ue013'), this.format2digits.format(minutes));
        }
        if (s.indexOf(57364) != -1 || s.indexOf(57365) != -1) {
            final float seconds = (float)(this.dateToBeFormatted * 24.0 * 60.0 * 60.0);
            s = s.replaceAll(String.valueOf('\ue014'), this.format1digit.format(seconds));
            s = s.replaceAll(String.valueOf('\ue015'), this.format2digits.format(seconds));
        }
        if (s.indexOf(57366) != -1 || s.indexOf(57367) != -1) {
            final float millisTemp = (float)((this.dateToBeFormatted - Math.floor(this.dateToBeFormatted)) * 24.0 * 60.0 * 60.0);
            final float millis = millisTemp - (int)millisTemp;
            s = s.replaceAll(String.valueOf('\ue016'), this.format3digit.format(millis * 10.0f));
            s = s.replaceAll(String.valueOf('\ue017'), this.format4digits.format(millis * 100.0f));
        }
        return new StringBuffer(s);
    }
}
