// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

import org.apache.poi.ss.formula.eval.NotImplementedException;
import java.text.ParsePosition;
import java.text.FieldPosition;
import org.apache.poi.ss.format.SimpleFraction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.Format;

public class FractionFormat extends Format
{
    private static final Pattern DENOM_FORMAT_PATTERN;
    private static final int MAX_DENOM_POW = 4;
    private final int exactDenom;
    private final int maxDenom;
    private final String wholePartFormatString;
    
    public FractionFormat(final String wholePartFormatString, final String denomFormatString) {
        this.wholePartFormatString = wholePartFormatString;
        final Matcher m = FractionFormat.DENOM_FORMAT_PATTERN.matcher(denomFormatString);
        int tmpExact = -1;
        int tmpMax = -1;
        if (m.find()) {
            if (m.group(2) != null) {
                try {
                    tmpExact = Integer.parseInt(m.group(2));
                    if (tmpExact == 0) {
                        tmpExact = -1;
                    }
                }
                catch (NumberFormatException e) {}
            }
            else if (m.group(1) != null) {
                int len = m.group(1).length();
                len = ((len > 4) ? 4 : len);
                tmpMax = (int)Math.pow(10.0, len);
            }
            else {
                tmpExact = 100;
            }
        }
        if (tmpExact <= 0 && tmpMax <= 0) {
            tmpExact = 100;
        }
        this.exactDenom = tmpExact;
        this.maxDenom = tmpMax;
    }
    
    public String format(final Number num) {
        final double doubleValue = num.doubleValue();
        final boolean isNeg = doubleValue < 0.0;
        final double absDoubleValue = Math.abs(doubleValue);
        final double wholePart = Math.floor(absDoubleValue);
        final double decPart = absDoubleValue - wholePart;
        if (wholePart + decPart == 0.0) {
            return "0";
        }
        if (absDoubleValue < 1 / Math.max(this.exactDenom, this.maxDenom)) {
            return "0";
        }
        if (wholePart + (int)decPart == wholePart + decPart) {
            final StringBuilder sb = new StringBuilder();
            if (isNeg) {
                sb.append("-");
            }
            sb.append(Integer.toString((int)wholePart));
            return sb.toString();
        }
        SimpleFraction fract = null;
        try {
            if (this.exactDenom > 0) {
                fract = SimpleFraction.buildFractionExactDenominator(decPart, this.exactDenom);
            }
            else {
                fract = SimpleFraction.buildFractionMaxDenominator(decPart, this.maxDenom);
            }
        }
        catch (RuntimeException e) {
            e.printStackTrace();
            return Double.toString(doubleValue);
        }
        final StringBuilder sb2 = new StringBuilder();
        if (isNeg) {
            sb2.append("-");
        }
        if ("".equals(this.wholePartFormatString)) {
            final int trueNum = fract.getDenominator() * (int)wholePart + fract.getNumerator();
            sb2.append(trueNum).append("/").append(fract.getDenominator());
            return sb2.toString();
        }
        if (fract.getNumerator() == 0) {
            sb2.append(Integer.toString((int)wholePart));
            return sb2.toString();
        }
        if (fract.getNumerator() == fract.getDenominator()) {
            sb2.append(Integer.toString((int)wholePart + 1));
            return sb2.toString();
        }
        if (wholePart > 0.0) {
            sb2.append(Integer.toString((int)wholePart)).append(" ");
        }
        sb2.append(fract.getNumerator()).append("/").append(fract.getDenominator());
        return sb2.toString();
    }
    
    @Override
    public StringBuffer format(final Object obj, final StringBuffer toAppendTo, final FieldPosition pos) {
        return toAppendTo.append(this.format((Number)obj));
    }
    
    @Override
    public Object parseObject(final String source, final ParsePosition pos) {
        throw new NotImplementedException("Reverse parsing not supported");
    }
    
    static {
        DENOM_FORMAT_PATTERN = Pattern.compile("(?:(#+)|(\\d+))");
    }
}
