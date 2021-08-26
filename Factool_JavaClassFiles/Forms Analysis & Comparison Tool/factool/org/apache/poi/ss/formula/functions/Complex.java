// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ValueEval;

public class Complex extends Var2or3ArgFunction implements FreeRefFunction
{
    public static final FreeRefFunction instance;
    public static final String DEFAULT_SUFFIX = "i";
    public static final String SUPPORTED_SUFFIX = "j";
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval real_num, final ValueEval i_num) {
        return this.evaluate(srcRowIndex, srcColumnIndex, real_num, i_num, new StringEval("i"));
    }
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval real_num, final ValueEval i_num, final ValueEval suffix) {
        ValueEval veText1;
        try {
            veText1 = OperandResolver.getSingleValue(real_num, srcRowIndex, srcColumnIndex);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        double realNum = 0.0;
        try {
            realNum = OperandResolver.coerceValueToDouble(veText1);
        }
        catch (EvaluationException e3) {
            return ErrorEval.VALUE_INVALID;
        }
        ValueEval veINum;
        try {
            veINum = OperandResolver.getSingleValue(i_num, srcRowIndex, srcColumnIndex);
        }
        catch (EvaluationException e2) {
            return e2.getErrorEval();
        }
        double realINum = 0.0;
        try {
            realINum = OperandResolver.coerceValueToDouble(veINum);
        }
        catch (EvaluationException e4) {
            return ErrorEval.VALUE_INVALID;
        }
        String suffixValue = OperandResolver.coerceValueToString(suffix);
        if (suffixValue.length() == 0) {
            suffixValue = "i";
        }
        if (suffixValue.equals("i".toUpperCase()) || suffixValue.equals("j".toUpperCase())) {
            return ErrorEval.VALUE_INVALID;
        }
        if (!suffixValue.equals("i") && !suffixValue.equals("j")) {
            return ErrorEval.VALUE_INVALID;
        }
        final StringBuffer strb = new StringBuffer("");
        if (realNum != 0.0) {
            if (this.isDoubleAnInt(realNum)) {
                strb.append(new Double(realNum).intValue());
            }
            else {
                strb.append(realNum);
            }
        }
        if (realINum != 0.0) {
            if (strb.length() != 0 && realINum > 0.0) {
                strb.append("+");
            }
            if (realINum != 1.0 && realINum != -1.0) {
                if (this.isDoubleAnInt(realINum)) {
                    strb.append(new Double(realINum).intValue());
                }
                else {
                    strb.append(realINum);
                }
            }
            strb.append(suffixValue);
        }
        return new StringEval(strb.toString());
    }
    
    private boolean isDoubleAnInt(final double number) {
        return number == Math.floor(number) && !Double.isInfinite(number);
    }
    
    @Override
    public ValueEval evaluate(final ValueEval[] args, final OperationEvaluationContext ec) {
        if (args.length == 2) {
            return this.evaluate(ec.getRowIndex(), ec.getColumnIndex(), args[0], args[1]);
        }
        if (args.length == 3) {
            return this.evaluate(ec.getRowIndex(), ec.getColumnIndex(), args[0], args[1], args[2]);
        }
        return ErrorEval.VALUE_INVALID;
    }
    
    static {
        instance = new Complex();
    }
}
