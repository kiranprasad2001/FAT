// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.format;

import java.util.WeakHashMap;
import java.awt.Color;
import javax.swing.JLabel;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;

public class CellFormat
{
    private final String format;
    private final CellFormatPart posNumFmt;
    private final CellFormatPart zeroNumFmt;
    private final CellFormatPart negNumFmt;
    private final CellFormatPart textFmt;
    private final int formatPartCount;
    private static final Pattern ONE_PART;
    private static final CellFormatPart DEFAULT_TEXT_FORMAT;
    private static final String INVALID_VALUE_FOR_FORMAT = "###############################################################################################################################################################################################################################################################";
    private static String QUOTE;
    public static final CellFormat GENERAL_FORMAT;
    private static final Map<String, CellFormat> formatCache;
    
    public static CellFormat getInstance(final String format) {
        CellFormat fmt = CellFormat.formatCache.get(format);
        if (fmt == null) {
            if (format.equals("General") || format.equals("@")) {
                fmt = CellFormat.GENERAL_FORMAT;
            }
            else {
                fmt = new CellFormat(format);
            }
            CellFormat.formatCache.put(format, fmt);
        }
        return fmt;
    }
    
    private CellFormat(final String format) {
        this.format = format;
        final Matcher m = CellFormat.ONE_PART.matcher(format);
        final List<CellFormatPart> parts = new ArrayList<CellFormatPart>();
        while (m.find()) {
            try {
                String valueDesc = m.group();
                if (valueDesc.endsWith(";")) {
                    valueDesc = valueDesc.substring(0, valueDesc.length() - 1);
                }
                parts.add(new CellFormatPart(valueDesc));
            }
            catch (RuntimeException e) {
                CellFormatter.logger.log(Level.WARNING, "Invalid format: " + CellFormatter.quote(m.group()), e);
                parts.add(null);
            }
        }
        switch (this.formatPartCount = parts.size()) {
            case 1: {
                this.posNumFmt = parts.get(0);
                this.negNumFmt = null;
                this.zeroNumFmt = null;
                this.textFmt = CellFormat.DEFAULT_TEXT_FORMAT;
                break;
            }
            case 2: {
                this.posNumFmt = parts.get(0);
                this.negNumFmt = parts.get(1);
                this.zeroNumFmt = null;
                this.textFmt = CellFormat.DEFAULT_TEXT_FORMAT;
                break;
            }
            case 3: {
                this.posNumFmt = parts.get(0);
                this.negNumFmt = parts.get(1);
                this.zeroNumFmt = parts.get(2);
                this.textFmt = CellFormat.DEFAULT_TEXT_FORMAT;
                break;
            }
            default: {
                this.posNumFmt = parts.get(0);
                this.negNumFmt = parts.get(1);
                this.zeroNumFmt = parts.get(2);
                this.textFmt = parts.get(3);
                break;
            }
        }
    }
    
    public CellFormatResult apply(final Object value) {
        if (value instanceof Number) {
            final Number num = (Number)value;
            final double val = num.doubleValue();
            if (val < 0.0 && ((this.formatPartCount == 2 && !this.posNumFmt.hasCondition() && !this.negNumFmt.hasCondition()) || (this.formatPartCount == 3 && !this.negNumFmt.hasCondition()) || (this.formatPartCount == 4 && !this.negNumFmt.hasCondition()))) {
                return this.negNumFmt.apply(-val);
            }
            return this.getApplicableFormatPart(val).apply(val);
        }
        else {
            if (!(value instanceof Date)) {
                return this.textFmt.apply(value);
            }
            final Double numericValue = DateUtil.getExcelDate((Date)value);
            if (DateUtil.isValidExcelDate(numericValue)) {
                return this.getApplicableFormatPart(numericValue).apply(value);
            }
            throw new IllegalArgumentException("value not a valid Excel date");
        }
    }
    
    private CellFormatResult apply(final Date date, final double numericValue) {
        return this.getApplicableFormatPart(numericValue).apply(date);
    }
    
    public CellFormatResult apply(final Cell c) {
        switch (ultimateType(c)) {
            case 3: {
                return this.apply("");
            }
            case 4: {
                return this.apply(c.getBooleanCellValue());
            }
            case 0: {
                final Double value = c.getNumericCellValue();
                if (this.getApplicableFormatPart(value).getCellFormatType() != CellFormatType.DATE) {
                    return this.apply(value);
                }
                if (DateUtil.isValidExcelDate(value)) {
                    return this.apply(c.getDateCellValue(), value);
                }
                return this.apply("###############################################################################################################################################################################################################################################################");
            }
            case 1: {
                return this.apply(c.getStringCellValue());
            }
            default: {
                return this.apply("?");
            }
        }
    }
    
    public CellFormatResult apply(final JLabel label, final Object value) {
        final CellFormatResult result = this.apply(value);
        label.setText(result.text);
        if (result.textColor != null) {
            label.setForeground(result.textColor);
        }
        return result;
    }
    
    private CellFormatResult apply(final JLabel label, final Date date, final double numericValue) {
        final CellFormatResult result = this.apply(date, numericValue);
        label.setText(result.text);
        if (result.textColor != null) {
            label.setForeground(result.textColor);
        }
        return result;
    }
    
    public CellFormatResult apply(final JLabel label, final Cell c) {
        switch (ultimateType(c)) {
            case 3: {
                return this.apply(label, "");
            }
            case 4: {
                return this.apply(label, c.getBooleanCellValue());
            }
            case 0: {
                final Double value = c.getNumericCellValue();
                if (this.getApplicableFormatPart(value).getCellFormatType() != CellFormatType.DATE) {
                    return this.apply(label, value);
                }
                if (DateUtil.isValidExcelDate(value)) {
                    return this.apply(label, c.getDateCellValue(), value);
                }
                return this.apply(label, "###############################################################################################################################################################################################################################################################");
            }
            case 1: {
                return this.apply(label, c.getStringCellValue());
            }
            default: {
                return this.apply(label, "?");
            }
        }
    }
    
    private CellFormatPart getApplicableFormatPart(final Object value) {
        if (!(value instanceof Number)) {
            throw new IllegalArgumentException("value must be a Number");
        }
        final double val = ((Number)value).doubleValue();
        if (this.formatPartCount == 1) {
            if (!this.posNumFmt.hasCondition() || (this.posNumFmt.hasCondition() && this.posNumFmt.applies(val))) {
                return this.posNumFmt;
            }
            return new CellFormatPart("General");
        }
        else if (this.formatPartCount == 2) {
            if ((!this.posNumFmt.hasCondition() && val >= 0.0) || (this.posNumFmt.hasCondition() && this.posNumFmt.applies(val))) {
                return this.posNumFmt;
            }
            if (!this.negNumFmt.hasCondition() || (this.negNumFmt.hasCondition() && this.negNumFmt.applies(val))) {
                return this.negNumFmt;
            }
            return new CellFormatPart(CellFormat.QUOTE + "###############################################################################################################################################################################################################################################################" + CellFormat.QUOTE);
        }
        else {
            if ((!this.posNumFmt.hasCondition() && val > 0.0) || (this.posNumFmt.hasCondition() && this.posNumFmt.applies(val))) {
                return this.posNumFmt;
            }
            if ((!this.negNumFmt.hasCondition() && val < 0.0) || (this.negNumFmt.hasCondition() && this.negNumFmt.applies(val))) {
                return this.negNumFmt;
            }
            return this.zeroNumFmt;
        }
    }
    
    public static int ultimateType(final Cell cell) {
        final int type = cell.getCellType();
        if (type == 2) {
            return cell.getCachedFormulaResultType();
        }
        return type;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof CellFormat) {
            final CellFormat that = (CellFormat)obj;
            return this.format.equals(that.format);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return this.format.hashCode();
    }
    
    static {
        ONE_PART = Pattern.compile(CellFormatPart.FORMAT_PAT.pattern() + "(;|$)", 6);
        DEFAULT_TEXT_FORMAT = new CellFormatPart("@");
        CellFormat.QUOTE = "\"";
        GENERAL_FORMAT = new CellFormat("General") {
            @Override
            public CellFormatResult apply(final Object value) {
                final String text = new CellGeneralFormatter().format(value);
                return new CellFormatResult(true, text, null);
            }
        };
        formatCache = new WeakHashMap<String, CellFormat>();
    }
}
