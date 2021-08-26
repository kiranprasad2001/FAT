// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.format;

import java.util.regex.Matcher;
import java.util.Calendar;
import java.util.Formatter;
import java.text.AttributedCharacterIterator;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DateFormat;

public class CellDateFormatter extends CellFormatter
{
    private boolean amPmUpper;
    private boolean showM;
    private boolean showAmPm;
    private final DateFormat dateFmt;
    private String sFmt;
    private static final long EXCEL_EPOCH_TIME;
    private static final Date EXCEL_EPOCH_DATE;
    private static final CellFormatter SIMPLE_DATE;
    
    public CellDateFormatter(final String format) {
        super(format);
        final DatePartHandler partHandler = new DatePartHandler();
        final StringBuffer descBuf = CellFormatPart.parseFormat(format, CellFormatType.DATE, partHandler);
        partHandler.finish(descBuf);
        final String ptrn = descBuf.toString().replaceAll("((y)(?!y))(?<!yy)", "yy");
        this.dateFmt = new SimpleDateFormat(ptrn, CellDateFormatter.LOCALE);
    }
    
    @Override
    public void formatValue(final StringBuffer toAppendTo, Object value) {
        if (value == null) {
            value = 0.0;
        }
        if (value instanceof Number) {
            final Number num = (Number)value;
            final double v = num.doubleValue();
            if (v == 0.0) {
                value = CellDateFormatter.EXCEL_EPOCH_DATE;
            }
            else {
                value = new Date((long)(CellDateFormatter.EXCEL_EPOCH_TIME + v));
            }
        }
        final AttributedCharacterIterator it = this.dateFmt.formatToCharacterIterator(value);
        boolean doneAm = false;
        boolean doneMillis = false;
        it.first();
        for (char ch = it.first(); ch != '\uffff'; ch = it.next()) {
            if (it.getAttribute(DateFormat.Field.MILLISECOND) != null) {
                if (!doneMillis) {
                    final Date dateObj = (Date)value;
                    final int pos = toAppendTo.length();
                    final Formatter formatter = new Formatter(toAppendTo);
                    try {
                        final long msecs = dateObj.getTime() % 1000L;
                        formatter.format(CellDateFormatter.LOCALE, this.sFmt, msecs / 1000.0);
                    }
                    finally {
                        formatter.close();
                    }
                    toAppendTo.delete(pos, pos + 2);
                    doneMillis = true;
                }
            }
            else if (it.getAttribute(DateFormat.Field.AM_PM) != null) {
                if (!doneAm) {
                    if (this.showAmPm) {
                        if (this.amPmUpper) {
                            toAppendTo.append(Character.toUpperCase(ch));
                            if (this.showM) {
                                toAppendTo.append('M');
                            }
                        }
                        else {
                            toAppendTo.append(Character.toLowerCase(ch));
                            if (this.showM) {
                                toAppendTo.append('m');
                            }
                        }
                    }
                    doneAm = true;
                }
            }
            else {
                toAppendTo.append(ch);
            }
        }
    }
    
    @Override
    public void simpleValue(final StringBuffer toAppendTo, final Object value) {
        CellDateFormatter.SIMPLE_DATE.formatValue(toAppendTo, value);
    }
    
    static {
        SIMPLE_DATE = new CellDateFormatter("mm/d/y");
        final Calendar c = Calendar.getInstance();
        c.set(1904, 0, 1, 0, 0, 0);
        EXCEL_EPOCH_DATE = c.getTime();
        EXCEL_EPOCH_TIME = c.getTimeInMillis();
    }
    
    private class DatePartHandler implements CellFormatPart.PartHandler
    {
        private int mStart;
        private int mLen;
        private int hStart;
        private int hLen;
        
        private DatePartHandler() {
            this.mStart = -1;
            this.hStart = -1;
        }
        
        @Override
        public String handlePart(final Matcher m, String part, final CellFormatType type, final StringBuffer desc) {
            final int pos = desc.length();
            final char firstCh = part.charAt(0);
            switch (firstCh) {
                case 'S':
                case 's': {
                    if (this.mStart >= 0) {
                        for (int i = 0; i < this.mLen; ++i) {
                            desc.setCharAt(this.mStart + i, 'm');
                        }
                        this.mStart = -1;
                    }
                    return part.toLowerCase();
                }
                case 'H':
                case 'h': {
                    this.mStart = -1;
                    this.hStart = pos;
                    this.hLen = part.length();
                    return part.toLowerCase();
                }
                case 'D':
                case 'd': {
                    this.mStart = -1;
                    if (part.length() <= 2) {
                        return part.toLowerCase();
                    }
                    return part.toLowerCase().replace('d', 'E');
                }
                case 'M':
                case 'm': {
                    this.mStart = pos;
                    this.mLen = part.length();
                    if (this.hStart >= 0) {
                        return part.toLowerCase();
                    }
                    return part.toUpperCase();
                }
                case 'Y':
                case 'y': {
                    this.mStart = -1;
                    if (part.length() == 3) {
                        part = "yyyy";
                    }
                    return part.toLowerCase();
                }
                case '0': {
                    this.mStart = -1;
                    final int sLen = part.length();
                    CellDateFormatter.this.sFmt = "%0" + (sLen + 2) + "." + sLen + "f";
                    return part.replace('0', 'S');
                }
                case 'A':
                case 'P':
                case 'a':
                case 'p': {
                    if (part.length() > 1) {
                        this.mStart = -1;
                        CellDateFormatter.this.showAmPm = true;
                        CellDateFormatter.this.showM = (Character.toLowerCase(part.charAt(1)) == 'm');
                        CellDateFormatter.this.amPmUpper = (CellDateFormatter.this.showM || Character.isUpperCase(part.charAt(0)));
                        return "a";
                    }
                    break;
                }
            }
            return null;
        }
        
        public void finish(final StringBuffer toAppendTo) {
            if (this.hStart >= 0 && !CellDateFormatter.this.showAmPm) {
                for (int i = 0; i < this.hLen; ++i) {
                    toAppendTo.setCharAt(this.hStart + i, 'H');
                }
            }
        }
    }
}
