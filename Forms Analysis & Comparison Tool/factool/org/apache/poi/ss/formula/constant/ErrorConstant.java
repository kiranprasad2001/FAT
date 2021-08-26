// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.constant;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.ss.usermodel.ErrorConstants;
import org.apache.poi.util.POILogger;

public class ErrorConstant
{
    private static POILogger logger;
    private static final ErrorConstants EC;
    private static final ErrorConstant NULL;
    private static final ErrorConstant DIV_0;
    private static final ErrorConstant VALUE;
    private static final ErrorConstant REF;
    private static final ErrorConstant NAME;
    private static final ErrorConstant NUM;
    private static final ErrorConstant NA;
    private final int _errorCode;
    
    private ErrorConstant(final int errorCode) {
        this._errorCode = errorCode;
    }
    
    public int getErrorCode() {
        return this._errorCode;
    }
    
    public String getText() {
        if (ErrorConstants.isValidCode(this._errorCode)) {
            return ErrorConstants.getText(this._errorCode);
        }
        return "unknown error code (" + this._errorCode + ")";
    }
    
    public static ErrorConstant valueOf(final int errorCode) {
        switch (errorCode) {
            case 0: {
                return ErrorConstant.NULL;
            }
            case 7: {
                return ErrorConstant.DIV_0;
            }
            case 15: {
                return ErrorConstant.VALUE;
            }
            case 23: {
                return ErrorConstant.REF;
            }
            case 29: {
                return ErrorConstant.NAME;
            }
            case 36: {
                return ErrorConstant.NUM;
            }
            case 42: {
                return ErrorConstant.NA;
            }
            default: {
                ErrorConstant.logger.log(5, "Warning - unexpected error code (" + errorCode + ")");
                return new ErrorConstant(errorCode);
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(64);
        sb.append(this.getClass().getName()).append(" [");
        sb.append(this.getText());
        sb.append("]");
        return sb.toString();
    }
    
    static {
        ErrorConstant.logger = POILogFactory.getLogger(ErrorConstant.class);
        EC = null;
        final ErrorConstants ec = ErrorConstant.EC;
        NULL = new ErrorConstant(0);
        final ErrorConstants ec2 = ErrorConstant.EC;
        DIV_0 = new ErrorConstant(7);
        final ErrorConstants ec3 = ErrorConstant.EC;
        VALUE = new ErrorConstant(15);
        final ErrorConstants ec4 = ErrorConstant.EC;
        REF = new ErrorConstant(23);
        final ErrorConstants ec5 = ErrorConstant.EC;
        NAME = new ErrorConstant(29);
        final ErrorConstants ec6 = ErrorConstant.EC;
        NUM = new ErrorConstant(36);
        final ErrorConstants ec7 = ErrorConstant.EC;
        NA = new ErrorConstant(42);
    }
}
