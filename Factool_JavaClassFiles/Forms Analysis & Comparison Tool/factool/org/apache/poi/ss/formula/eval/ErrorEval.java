// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.eval;

import java.util.HashMap;
import org.apache.poi.ss.usermodel.FormulaError;
import java.util.Map;

public final class ErrorEval implements ValueEval
{
    private static final Map<FormulaError, ErrorEval> evals;
    public static final ErrorEval NULL_INTERSECTION;
    public static final ErrorEval DIV_ZERO;
    public static final ErrorEval VALUE_INVALID;
    public static final ErrorEval REF_INVALID;
    public static final ErrorEval NAME_INVALID;
    public static final ErrorEval NUM_ERROR;
    public static final ErrorEval NA;
    public static final ErrorEval FUNCTION_NOT_IMPLEMENTED;
    public static final ErrorEval CIRCULAR_REF_ERROR;
    private FormulaError _error;
    
    public static ErrorEval valueOf(final int errorCode) {
        final FormulaError error = FormulaError.forInt(errorCode);
        final ErrorEval eval = ErrorEval.evals.get(error);
        if (eval != null) {
            return eval;
        }
        throw new RuntimeException("Unhandled error type " + eval + " for code " + errorCode);
    }
    
    public static String getText(final int errorCode) {
        if (FormulaError.isValidCode(errorCode)) {
            return FormulaError.forInt(errorCode).getString();
        }
        return "~non~std~err(" + errorCode + ")~";
    }
    
    private ErrorEval(final FormulaError error) {
        this._error = error;
        ErrorEval.evals.put(error, this);
    }
    
    public int getErrorCode() {
        return this._error.getLongCode();
    }
    
    public String getErrorString() {
        return this._error.getString();
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(64);
        sb.append(this.getClass().getName()).append(" [");
        sb.append(this._error.getString());
        sb.append("]");
        return sb.toString();
    }
    
    static {
        evals = new HashMap<FormulaError, ErrorEval>();
        NULL_INTERSECTION = new ErrorEval(FormulaError.NULL);
        DIV_ZERO = new ErrorEval(FormulaError.DIV0);
        VALUE_INVALID = new ErrorEval(FormulaError.VALUE);
        REF_INVALID = new ErrorEval(FormulaError.REF);
        NAME_INVALID = new ErrorEval(FormulaError.NAME);
        NUM_ERROR = new ErrorEval(FormulaError.NUM);
        NA = new ErrorEval(FormulaError.NA);
        FUNCTION_NOT_IMPLEMENTED = new ErrorEval(FormulaError.FUNCTION_NOT_IMPLEMENTED);
        CIRCULAR_REF_ERROR = new ErrorEval(FormulaError.CIRCULAR_REF);
    }
}
