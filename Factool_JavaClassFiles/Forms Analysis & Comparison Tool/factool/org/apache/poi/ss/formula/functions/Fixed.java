// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.StringEval;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.math.RoundingMode;
import java.math.BigDecimal;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class Fixed implements Function1Arg, Function2Arg, Function3Arg
{
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1, final ValueEval arg2) {
        return this.fixed(arg0, arg1, arg2, srcRowIndex, srcColumnIndex);
    }
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1) {
        return this.fixed(arg0, arg1, BoolEval.FALSE, srcRowIndex, srcColumnIndex);
    }
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0) {
        return this.fixed(arg0, new NumberEval(2.0), BoolEval.FALSE, srcRowIndex, srcColumnIndex);
    }
    
    @Override
    public ValueEval evaluate(final ValueEval[] args, final int srcRowIndex, final int srcColumnIndex) {
        switch (args.length) {
            case 1: {
                return this.fixed(args[0], new NumberEval(2.0), BoolEval.FALSE, srcRowIndex, srcColumnIndex);
            }
            case 2: {
                return this.fixed(args[0], args[1], BoolEval.FALSE, srcRowIndex, srcColumnIndex);
            }
            case 3: {
                return this.fixed(args[0], args[1], args[2], srcRowIndex, srcColumnIndex);
            }
            default: {
                return ErrorEval.VALUE_INVALID;
            }
        }
    }
    
    private ValueEval fixed(final ValueEval numberParam, final ValueEval placesParam, final ValueEval skipThousandsSeparatorParam, final int srcRowIndex, final int srcColumnIndex) {
        try {
            final ValueEval numberValueEval = OperandResolver.getSingleValue(numberParam, srcRowIndex, srcColumnIndex);
            BigDecimal number = new BigDecimal(OperandResolver.coerceValueToDouble(numberValueEval));
            final ValueEval placesValueEval = OperandResolver.getSingleValue(placesParam, srcRowIndex, srcColumnIndex);
            final int places = OperandResolver.coerceValueToInt(placesValueEval);
            final ValueEval skipThousandsSeparatorValueEval = OperandResolver.getSingleValue(skipThousandsSeparatorParam, srcRowIndex, srcColumnIndex);
            final Boolean skipThousandsSeparator = OperandResolver.coerceValueToBoolean(skipThousandsSeparatorValueEval, false);
            number = number.setScale(places, RoundingMode.HALF_UP);
            final NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
            final DecimalFormat formatter = (DecimalFormat)nf;
            formatter.setGroupingUsed(!skipThousandsSeparator);
            formatter.setMinimumFractionDigits((places >= 0) ? places : 0);
            formatter.setMaximumFractionDigits((places >= 0) ? places : 0);
            final String numberString = formatter.format(number.doubleValue());
            return new StringEval(numberString);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }
}
