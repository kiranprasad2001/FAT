// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class Replace extends Fixed4ArgFunction
{
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1, final ValueEval arg2, final ValueEval arg3) {
        String oldStr;
        int startNum;
        int numChars;
        String newStr;
        try {
            oldStr = TextFunction.evaluateStringArg(arg0, srcRowIndex, srcColumnIndex);
            startNum = TextFunction.evaluateIntArg(arg1, srcRowIndex, srcColumnIndex);
            numChars = TextFunction.evaluateIntArg(arg2, srcRowIndex, srcColumnIndex);
            newStr = TextFunction.evaluateStringArg(arg3, srcRowIndex, srcColumnIndex);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        if (startNum < 1 || numChars < 0) {
            return ErrorEval.VALUE_INVALID;
        }
        final StringBuffer strBuff = new StringBuffer(oldStr);
        if (startNum <= oldStr.length() && numChars != 0) {
            strBuff.delete(startNum - 1, startNum - 1 + numChars);
        }
        if (startNum > strBuff.length()) {
            strBuff.append(newStr);
        }
        else {
            strBuff.insert(startNum - 1, newStr);
        }
        return new StringEval(strBuff.toString());
    }
}
