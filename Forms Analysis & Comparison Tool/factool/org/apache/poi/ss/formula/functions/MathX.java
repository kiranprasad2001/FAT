// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import java.math.RoundingMode;
import java.math.BigDecimal;
import org.apache.poi.ss.util.NumberToTextConverter;

final class MathX
{
    private MathX() {
    }
    
    public static double round(final double n, final int p) {
        double retval;
        if (Double.isNaN(n) || Double.isInfinite(n)) {
            retval = Double.NaN;
        }
        else {
            retval = new BigDecimal(NumberToTextConverter.toText(n)).setScale(p, RoundingMode.HALF_UP).doubleValue();
        }
        return retval;
    }
    
    public static double roundUp(final double n, final int p) {
        double retval;
        if (Double.isNaN(n) || Double.isInfinite(n)) {
            retval = Double.NaN;
        }
        else {
            retval = BigDecimal.valueOf(n).setScale(p, RoundingMode.UP).doubleValue();
        }
        return retval;
    }
    
    public static double roundDown(final double n, final int p) {
        double retval;
        if (Double.isNaN(n) || Double.isInfinite(n)) {
            retval = Double.NaN;
        }
        else {
            retval = BigDecimal.valueOf(n).setScale(p, RoundingMode.DOWN).doubleValue();
        }
        return retval;
    }
    
    public static short sign(final double d) {
        return (short)((d == 0.0) ? 0 : ((d < 0.0) ? -1 : 1));
    }
    
    public static double average(final double[] values) {
        double ave = 0.0;
        double sum = 0.0;
        for (int i = 0, iSize = values.length; i < iSize; ++i) {
            sum += values[i];
        }
        ave = sum / values.length;
        return ave;
    }
    
    public static double sum(final double[] values) {
        double sum = 0.0;
        for (int i = 0, iSize = values.length; i < iSize; ++i) {
            sum += values[i];
        }
        return sum;
    }
    
    public static double sumsq(final double[] values) {
        double sumsq = 0.0;
        for (int i = 0, iSize = values.length; i < iSize; ++i) {
            sumsq += values[i] * values[i];
        }
        return sumsq;
    }
    
    public static double product(final double[] values) {
        double product = 0.0;
        if (values != null && values.length > 0) {
            product = 1.0;
            for (int i = 0, iSize = values.length; i < iSize; ++i) {
                product *= values[i];
            }
        }
        return product;
    }
    
    public static double min(final double[] values) {
        double min = Double.POSITIVE_INFINITY;
        for (int i = 0, iSize = values.length; i < iSize; ++i) {
            min = Math.min(min, values[i]);
        }
        return min;
    }
    
    public static double max(final double[] values) {
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0, iSize = values.length; i < iSize; ++i) {
            max = Math.max(max, values[i]);
        }
        return max;
    }
    
    public static double floor(final double n, final double s) {
        double f;
        if ((n < 0.0 && s > 0.0) || (n > 0.0 && s < 0.0) || (s == 0.0 && n != 0.0)) {
            f = Double.NaN;
        }
        else {
            f = ((n == 0.0 || s == 0.0) ? 0.0 : (Math.floor(n / s) * s));
        }
        return f;
    }
    
    public static double ceiling(final double n, final double s) {
        double c;
        if ((n < 0.0 && s > 0.0) || (n > 0.0 && s < 0.0)) {
            c = Double.NaN;
        }
        else {
            c = ((n == 0.0 || s == 0.0) ? 0.0 : (Math.ceil(n / s) * s));
        }
        return c;
    }
    
    public static double factorial(final int n) {
        double d = 1.0;
        if (n >= 0) {
            if (n <= 170) {
                for (int i = 1; i <= n; ++i) {
                    d *= i;
                }
            }
            else {
                d = Double.POSITIVE_INFINITY;
            }
        }
        else {
            d = Double.NaN;
        }
        return d;
    }
    
    public static double mod(final double n, final double d) {
        double result = 0.0;
        if (d == 0.0) {
            result = Double.NaN;
        }
        else if (sign(n) == sign(d)) {
            result = n % d;
        }
        else {
            result = (n % d + d) % d;
        }
        return result;
    }
    
    public static double acosh(final double d) {
        return Math.log(Math.sqrt(Math.pow(d, 2.0) - 1.0) + d);
    }
    
    public static double asinh(final double d) {
        return Math.log(Math.sqrt(d * d + 1.0) + d);
    }
    
    public static double atanh(final double d) {
        return Math.log((1.0 + d) / (1.0 - d)) / 2.0;
    }
    
    public static double cosh(final double d) {
        final double ePowX = Math.pow(2.718281828459045, d);
        final double ePowNegX = Math.pow(2.718281828459045, -d);
        return (ePowX + ePowNegX) / 2.0;
    }
    
    public static double sinh(final double d) {
        final double ePowX = Math.pow(2.718281828459045, d);
        final double ePowNegX = Math.pow(2.718281828459045, -d);
        return (ePowX - ePowNegX) / 2.0;
    }
    
    public static double tanh(final double d) {
        final double ePowX = Math.pow(2.718281828459045, d);
        final double ePowNegX = Math.pow(2.718281828459045, -d);
        return (ePowX - ePowNegX) / (ePowX + ePowNegX);
    }
    
    public static double nChooseK(final int n, final int k) {
        double d = 1.0;
        if (n < 0 || k < 0 || n < k) {
            d = Double.NaN;
        }
        else {
            final int minnk = Math.min(n - k, k);
            int i;
            for (int maxnk = i = Math.max(n - k, k); i < n; ++i) {
                d *= i + 1;
            }
            d /= factorial(minnk);
        }
        return d;
    }
}
