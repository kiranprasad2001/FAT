// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

public class Finance
{
    public static double pmt(final double r, final int nper, final double pv, final double fv, final int type) {
        final double pmt = -r * (pv * Math.pow(1.0 + r, nper) + fv) / ((1.0 + r * type) * (Math.pow(1.0 + r, nper) - 1.0));
        return pmt;
    }
    
    public static double pmt(final double r, final int nper, final double pv, final double fv) {
        return pmt(r, nper, pv, fv, 0);
    }
    
    public static double pmt(final double r, final int nper, final double pv) {
        return pmt(r, nper, pv, 0.0);
    }
    
    public static double ipmt(final double r, final int per, final int nper, final double pv, final double fv, final int type) {
        double ipmt = fv(r, per - 1, pmt(r, nper, pv, fv, type), pv, type) * r;
        if (type == 1) {
            ipmt /= 1.0 + r;
        }
        return ipmt;
    }
    
    public static double ipmt(final double r, final int per, final int nper, final double pv, final double fv) {
        return ipmt(r, per, nper, pv, fv, 0);
    }
    
    public static double ipmt(final double r, final int per, final int nper, final double pv) {
        return ipmt(r, per, nper, pv, 0.0);
    }
    
    public static double ppmt(final double r, final int per, final int nper, final double pv, final double fv, final int type) {
        return pmt(r, nper, pv, fv, type) - ipmt(r, per, nper, pv, fv, type);
    }
    
    public static double ppmt(final double r, final int per, final int nper, final double pv, final double fv) {
        return pmt(r, nper, pv, fv) - ipmt(r, per, nper, pv, fv);
    }
    
    public static double ppmt(final double r, final int per, final int nper, final double pv) {
        return pmt(r, nper, pv) - ipmt(r, per, nper, pv);
    }
    
    public static double fv(final double r, final int nper, final double pmt, final double pv, final int type) {
        final double fv = -(pv * Math.pow(1.0 + r, nper) + pmt * (1.0 + r * type) * (Math.pow(1.0 + r, nper) - 1.0) / r);
        return fv;
    }
    
    public static double fv(final double r, final int nper, final double c, final double pv) {
        return fv(r, nper, c, pv, 0);
    }
}
