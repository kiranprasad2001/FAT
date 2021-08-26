// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class Value extends Fixed1ArgFunction
{
    private static final int MIN_DISTANCE_BETWEEN_THOUSANDS_SEPARATOR = 4;
    private static final Double ZERO;
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0) {
        ValueEval veText;
        try {
            veText = OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        final String strText = OperandResolver.coerceValueToString(veText);
        final Double result = convertTextToNumber(strText);
        if (result == null) {
            return ErrorEval.VALUE_INVALID;
        }
        return new NumberEval(result);
    }
    
    private static Double convertTextToNumber(final String strText) {
        boolean foundCurrency = false;
        boolean foundUnaryPlus = false;
        boolean foundUnaryMinus = false;
        boolean foundPercentage = false;
        int len;
        int i;
        for (len = strText.length(), i = 0; i < len; ++i) {
            final char ch = strText.charAt(i);
            if (Character.isDigit(ch)) {
                break;
            }
            if (ch == '.') {
                break;
            }
            switch (ch) {
                case ' ': {
                    break;
                }
                case '$': {
                    if (foundCurrency) {
                        return null;
                    }
                    foundCurrency = true;
                    break;
                }
                case '+': {
                    if (foundUnaryMinus || foundUnaryPlus) {
                        return null;
                    }
                    foundUnaryPlus = true;
                    break;
                }
                case '-': {
                    if (foundUnaryMinus || foundUnaryPlus) {
                        return null;
                    }
                    foundUnaryMinus = true;
                    break;
                }
                default: {
                    return null;
                }
            }
        }
        if (i >= len) {
            if (foundCurrency || foundUnaryMinus || foundUnaryPlus) {
                return null;
            }
            return Value.ZERO;
        }
        else {
            boolean foundDecimalPoint = false;
            int lastThousandsSeparatorIndex = -32768;
            final StringBuffer sb = new StringBuffer(len);
            while (i < len) {
                final char ch2 = strText.charAt(i);
                if (Character.isDigit(ch2)) {
                    sb.append(ch2);
                }
                else {
                    switch (ch2) {
                        case ' ': {
                            final String remainingTextTrimmed = strText.substring(i).trim();
                            if (remainingTextTrimmed.equals("%")) {
                                foundPercentage = true;
                                break;
                            }
                            if (remainingTextTrimmed.length() > 0) {
                                return null;
                            }
                            break;
                        }
                        case '.': {
                            if (foundDecimalPoint) {
                                return null;
                            }
                            if (i - lastThousandsSeparatorIndex < 4) {
                                return null;
                            }
                            foundDecimalPoint = true;
                            sb.append('.');
                            break;
                        }
                        case ',': {
                            if (foundDecimalPoint) {
                                return null;
                            }
                            final int distanceBetweenThousandsSeparators = i - lastThousandsSeparatorIndex;
                            if (distanceBetweenThousandsSeparators < 4) {
                                return null;
                            }
                            lastThousandsSeparatorIndex = i;
                            break;
                        }
                        case 'E':
                        case 'e': {
                            if (i - lastThousandsSeparatorIndex < 4) {
                                return null;
                            }
                            sb.append(strText.substring(i));
                            i = len;
                            break;
                        }
                        case '%': {
                            foundPercentage = true;
                            break;
                        }
                        default: {
                            return null;
                        }
                    }
                }
                ++i;
            }
            if (!foundDecimalPoint && i - lastThousandsSeparatorIndex < 4) {
                return null;
            }
            double d;
            try {
                d = Double.parseDouble(sb.toString());
            }
            catch (NumberFormatException e) {
                return null;
            }
            final Double result = new Double(foundUnaryMinus ? (-d) : d);
            return foundPercentage ? (result / 100.0) : result;
        }
    }
    
    static {
        ZERO = new Double(0.0);
    }
}
