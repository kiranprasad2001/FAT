// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

public final class FinanceLib
{
    private FinanceLib() {
    }
    
    public static double fv(final double r, final double n, final double y, final double p, final boolean t) {
        double retval = 0.0;
        if (r == 0.0) {
            retval = -1.0 * (p + n * y);
        }
        else {
            final double r2 = r + 1.0;
            retval = (1.0 - Math.pow(r2, n)) * (t ? r2 : 1.0) * y / r - p * Math.pow(r2, n);
        }
        return retval;
    }
    
    public static double pv(final double r, final double n, final double y, final double f, final boolean t) {
        double retval = 0.0;
        if (r == 0.0) {
            retval = -1.0 * (n * y + f);
        }
        else {
            final double r2 = r + 1.0;
            retval = ((1.0 - Math.pow(r2, n)) / r * (t ? r2 : 1.0) * y - f) / Math.pow(r2, n);
        }
        return retval;
    }
    
    public static double npv(final double r, final double[] cfs) {
        double npv = 0.0;
        double trate;
        final double r2 = trate = r + 1.0;
        for (int i = 0, iSize = cfs.length; i < iSize; ++i) {
            npv += cfs[i] / trate;
            trate *= r2;
        }
        return npv;
    }
    
    public static double pmt(final double r, final double n, final double p, final double f, final boolean t) {
        double retval = 0.0;
        if (r == 0.0) {
            retval = -1.0 * (f + p) / n;
        }
        else {
            final double r2 = r + 1.0;
            retval = (f + p * Math.pow(r2, n)) * r / ((t ? r2 : 1.0) * (1.0 - Math.pow(r2, n)));
        }
        return retval;
    }
    
    public static double nper(final double r, final double y, final double p, final double f, final boolean t) {
        double retval = 0.0;
        if (r == 0.0) {
            retval = -1.0 * (f + p) / y;
        }
        else {
            final double r2 = r + 1.0;
            final double ryr = (t ? r2 : 1.0) * y / r;
            final double a1 = (ryr - f < 0.0) ? Math.log(f - ryr) : Math.log(ryr - f);
            final double a2 = (ryr - f < 0.0) ? Math.log(-p - ryr) : Math.log(p + ryr);
            final double a3 = Math.log(r2);
            retval = (a1 - a2) / a3;
        }
        return retval;
    }
}
