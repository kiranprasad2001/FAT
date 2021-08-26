// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.ptg;

import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.ss.usermodel.ErrorConstants;

public final class ErrPtg extends ScalarConstantPtg
{
    private static final ErrorConstants EC;
    public static final ErrPtg NULL_INTERSECTION;
    public static final ErrPtg DIV_ZERO;
    public static final ErrPtg VALUE_INVALID;
    public static final ErrPtg REF_INVALID;
    public static final ErrPtg NAME_INVALID;
    public static final ErrPtg NUM_ERROR;
    public static final ErrPtg N_A;
    public static final short sid = 28;
    private static final int SIZE = 2;
    private final int field_1_error_code;
    
    private ErrPtg(final int errorCode) {
        if (!ErrorConstants.isValidCode(errorCode)) {
            throw new IllegalArgumentException("Invalid error code (" + errorCode + ")");
        }
        this.field_1_error_code = errorCode;
    }
    
    public static ErrPtg read(final LittleEndianInput in) {
        return valueOf(in.readByte());
    }
    
    @Override
    public void write(final LittleEndianOutput out) {
        out.writeByte(28 + this.getPtgClass());
        out.writeByte(this.field_1_error_code);
    }
    
    @Override
    public String toFormulaString() {
        return ErrorConstants.getText(this.field_1_error_code);
    }
    
    @Override
    public int getSize() {
        return 2;
    }
    
    public int getErrorCode() {
        return this.field_1_error_code;
    }
    
    public static ErrPtg valueOf(final int code) {
        switch (code) {
            case 7: {
                return ErrPtg.DIV_ZERO;
            }
            case 42: {
                return ErrPtg.N_A;
            }
            case 29: {
                return ErrPtg.NAME_INVALID;
            }
            case 0: {
                return ErrPtg.NULL_INTERSECTION;
            }
            case 36: {
                return ErrPtg.NUM_ERROR;
            }
            case 23: {
                return ErrPtg.REF_INVALID;
            }
            case 15: {
                return ErrPtg.VALUE_INVALID;
            }
            default: {
                throw new RuntimeException("Unexpected error code (" + code + ")");
            }
        }
    }
    
    static {
        EC = null;
        final ErrorConstants ec = ErrPtg.EC;
        NULL_INTERSECTION = new ErrPtg(0);
        final ErrorConstants ec2 = ErrPtg.EC;
        DIV_ZERO = new ErrPtg(7);
        final ErrorConstants ec3 = ErrPtg.EC;
        VALUE_INVALID = new ErrPtg(15);
        final ErrorConstants ec4 = ErrPtg.EC;
        REF_INVALID = new ErrPtg(23);
        final ErrorConstants ec5 = ErrPtg.EC;
        NAME_INVALID = new ErrPtg(29);
        final ErrorConstants ec6 = ErrPtg.EC;
        NUM_ERROR = new ErrPtg(36);
        final ErrorConstants ec7 = ErrPtg.EC;
        N_A = new ErrPtg(42);
    }
}
