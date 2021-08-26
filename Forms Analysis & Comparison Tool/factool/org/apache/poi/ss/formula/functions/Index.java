// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.MissingArgEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.TwoDEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class Index implements Function2Arg, Function3Arg, Function4Arg
{
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1) {
        final TwoDEval reference = convertFirstArg(arg0);
        int columnIx = 0;
        try {
            int rowIx = resolveIndexArg(arg1, srcRowIndex, srcColumnIndex);
            if (!reference.isColumn()) {
                if (!reference.isRow()) {
                    return ErrorEval.REF_INVALID;
                }
                columnIx = rowIx;
                rowIx = 0;
            }
            return getValueFromArea(reference, rowIx, columnIx);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1, final ValueEval arg2) {
        final TwoDEval reference = convertFirstArg(arg0);
        try {
            final int columnIx = resolveIndexArg(arg2, srcRowIndex, srcColumnIndex);
            final int rowIx = resolveIndexArg(arg1, srcRowIndex, srcColumnIndex);
            return getValueFromArea(reference, rowIx, columnIx);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1, final ValueEval arg2, final ValueEval arg3) {
        throw new RuntimeException("Incomplete code - don't know how to support the 'area_num' parameter yet)");
    }
    
    private static TwoDEval convertFirstArg(final ValueEval arg0) {
        final ValueEval firstArg = arg0;
        if (firstArg instanceof RefEval) {
            return ((RefEval)firstArg).offset(0, 0, 0, 0);
        }
        if (firstArg instanceof TwoDEval) {
            return (TwoDEval)firstArg;
        }
        throw new RuntimeException("Incomplete code - cannot handle first arg of type (" + firstArg.getClass().getName() + ")");
    }
    
    @Override
    public ValueEval evaluate(final ValueEval[] args, final int srcRowIndex, final int srcColumnIndex) {
        switch (args.length) {
            case 2: {
                return this.evaluate(srcRowIndex, srcColumnIndex, args[0], args[1]);
            }
            case 3: {
                return this.evaluate(srcRowIndex, srcColumnIndex, args[0], args[1], args[2]);
            }
            case 4: {
                return this.evaluate(srcRowIndex, srcColumnIndex, args[0], args[1], args[2], args[3]);
            }
            default: {
                return ErrorEval.VALUE_INVALID;
            }
        }
    }
    
    private static ValueEval getValueFromArea(final TwoDEval ae, final int pRowIx, final int pColumnIx) throws EvaluationException {
        assert pRowIx >= 0;
        assert pColumnIx >= 0;
        TwoDEval result = ae;
        if (pRowIx != 0) {
            if (pRowIx > ae.getHeight()) {
                throw new EvaluationException(ErrorEval.REF_INVALID);
            }
            result = result.getRow(pRowIx - 1);
        }
        if (pColumnIx != 0) {
            if (pColumnIx > ae.getWidth()) {
                throw new EvaluationException(ErrorEval.REF_INVALID);
            }
            result = result.getColumn(pColumnIx - 1);
        }
        return result;
    }
    
    private static int resolveIndexArg(final ValueEval arg, final int srcCellRow, final int srcCellCol) throws EvaluationException {
        final ValueEval ev = OperandResolver.getSingleValue(arg, srcCellRow, srcCellCol);
        if (ev == MissingArgEval.instance) {
            return 0;
        }
        if (ev == BlankEval.instance) {
            return 0;
        }
        final int result = OperandResolver.coerceValueToInt(ev);
        if (result < 0) {
            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        }
        return result;
    }
}
