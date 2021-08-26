// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.SheetNameFormatter;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.MissingArgEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.ValueEval;

public class Address implements Function
{
    public static final int REF_ABSOLUTE = 1;
    public static final int REF_ROW_ABSOLUTE_COLUMN_RELATIVE = 2;
    public static final int REF_ROW_RELATIVE_RELATIVE_ABSOLUTE = 3;
    public static final int REF_RELATIVE = 4;
    
    @Override
    public ValueEval evaluate(final ValueEval[] args, final int srcRowIndex, final int srcColumnIndex) {
        if (args.length < 2 || args.length > 5) {
            return ErrorEval.VALUE_INVALID;
        }
        try {
            final int row = (int)NumericFunction.singleOperandEvaluate(args[0], srcRowIndex, srcColumnIndex);
            final int col = (int)NumericFunction.singleOperandEvaluate(args[1], srcRowIndex, srcColumnIndex);
            int refType;
            if (args.length > 2 && args[2] != MissingArgEval.instance) {
                refType = (int)NumericFunction.singleOperandEvaluate(args[2], srcRowIndex, srcColumnIndex);
            }
            else {
                refType = 1;
            }
            boolean pAbsRow = false;
            boolean pAbsCol = false;
            switch (refType) {
                case 1: {
                    pAbsRow = true;
                    pAbsCol = true;
                    break;
                }
                case 2: {
                    pAbsRow = true;
                    pAbsCol = false;
                    break;
                }
                case 3: {
                    pAbsRow = false;
                    pAbsCol = true;
                    break;
                }
                case 4: {
                    pAbsRow = false;
                    pAbsCol = false;
                    break;
                }
                default: {
                    throw new EvaluationException(ErrorEval.VALUE_INVALID);
                }
            }
            if (args.length > 3) {
                final ValueEval ve = OperandResolver.getSingleValue(args[3], srcRowIndex, srcColumnIndex);
                final boolean a1 = ve == MissingArgEval.instance || OperandResolver.coerceValueToBoolean(ve, false);
            }
            else {
                final boolean a1 = true;
            }
            String sheetName;
            if (args.length == 5) {
                final ValueEval ve2 = OperandResolver.getSingleValue(args[4], srcRowIndex, srcColumnIndex);
                sheetName = ((ve2 == MissingArgEval.instance) ? null : OperandResolver.coerceValueToString(ve2));
            }
            else {
                sheetName = null;
            }
            final CellReference ref = new CellReference(row - 1, col - 1, pAbsRow, pAbsCol);
            final StringBuffer sb = new StringBuffer(32);
            if (sheetName != null) {
                SheetNameFormatter.appendFormat(sb, sheetName);
                sb.append('!');
            }
            sb.append(ref.formatAsString());
            return new StringEval(sb.toString());
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }
}
