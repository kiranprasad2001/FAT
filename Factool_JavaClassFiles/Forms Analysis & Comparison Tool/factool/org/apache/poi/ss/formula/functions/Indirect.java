// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.MissingArgEval;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class Indirect implements FreeRefFunction
{
    public static final FreeRefFunction instance;
    
    private Indirect() {
    }
    
    @Override
    public ValueEval evaluate(final ValueEval[] args, final OperationEvaluationContext ec) {
        if (args.length < 1) {
            return ErrorEval.VALUE_INVALID;
        }
        String text;
        boolean isA1style = false;
        try {
            final ValueEval ve = OperandResolver.getSingleValue(args[0], ec.getRowIndex(), ec.getColumnIndex());
            text = OperandResolver.coerceValueToString(ve);
            switch (args.length) {
                case 1: {
                    isA1style = true;
                    break;
                }
                case 2: {
                    isA1style = evaluateBooleanArg(args[1], ec);
                    break;
                }
                default: {
                    return ErrorEval.VALUE_INVALID;
                }
            }
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        return evaluateIndirect(ec, text, isA1style);
    }
    
    private static boolean evaluateBooleanArg(final ValueEval arg, final OperationEvaluationContext ec) throws EvaluationException {
        final ValueEval ve = OperandResolver.getSingleValue(arg, ec.getRowIndex(), ec.getColumnIndex());
        return ve != BlankEval.instance && ve != MissingArgEval.instance && OperandResolver.coerceValueToBoolean(ve, false);
    }
    
    private static ValueEval evaluateIndirect(final OperationEvaluationContext ec, final String text, final boolean isA1style) {
        final int plingPos = text.lastIndexOf(33);
        String workbookName;
        String sheetName;
        String refText;
        if (plingPos < 0) {
            workbookName = null;
            sheetName = null;
            refText = text;
        }
        else {
            final String[] parts = parseWorkbookAndSheetName(text.subSequence(0, plingPos));
            if (parts == null) {
                return ErrorEval.REF_INVALID;
            }
            workbookName = parts[0];
            sheetName = parts[1];
            refText = text.substring(plingPos + 1);
        }
        final int colonPos = refText.indexOf(58);
        String refStrPart1;
        String refStrPart2;
        if (colonPos < 0) {
            refStrPart1 = refText.trim();
            refStrPart2 = null;
        }
        else {
            refStrPart1 = refText.substring(0, colonPos).trim();
            refStrPart2 = refText.substring(colonPos + 1).trim();
        }
        return ec.getDynamicReference(workbookName, sheetName, refStrPart1, refStrPart2, isA1style);
    }
    
    private static String[] parseWorkbookAndSheetName(final CharSequence text) {
        final int lastIx = text.length() - 1;
        if (lastIx < 0) {
            return null;
        }
        if (canTrim(text)) {
            return null;
        }
        char firstChar = text.charAt(0);
        if (Character.isWhitespace(firstChar)) {
            return null;
        }
        if (firstChar == '\'') {
            if (text.charAt(lastIx) != '\'') {
                return null;
            }
            firstChar = text.charAt(1);
            if (Character.isWhitespace(firstChar)) {
                return null;
            }
            String wbName;
            int sheetStartPos;
            if (firstChar == '[') {
                final int rbPos = text.toString().lastIndexOf(93);
                if (rbPos < 0) {
                    return null;
                }
                wbName = unescapeString(text.subSequence(2, rbPos));
                if (wbName == null || canTrim(wbName)) {
                    return null;
                }
                sheetStartPos = rbPos + 1;
            }
            else {
                wbName = null;
                sheetStartPos = 1;
            }
            final String sheetName = unescapeString(text.subSequence(sheetStartPos, lastIx));
            if (sheetName == null) {
                return null;
            }
            return new String[] { wbName, sheetName };
        }
        else {
            if (firstChar != '[') {
                return new String[] { null, text.toString() };
            }
            final int rbPos2 = text.toString().lastIndexOf(93);
            if (rbPos2 < 0) {
                return null;
            }
            final CharSequence wbName2 = text.subSequence(1, rbPos2);
            if (canTrim(wbName2)) {
                return null;
            }
            final CharSequence sheetName2 = text.subSequence(rbPos2 + 1, text.length());
            if (canTrim(sheetName2)) {
                return null;
            }
            return new String[] { wbName2.toString(), sheetName2.toString() };
        }
    }
    
    private static String unescapeString(final CharSequence text) {
        final int len = text.length();
        final StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; ++i) {
            char ch = text.charAt(i);
            if (ch == '\'') {
                if (++i >= len) {
                    return null;
                }
                ch = text.charAt(i);
                if (ch != '\'') {
                    return null;
                }
            }
            sb.append(ch);
        }
        return sb.toString();
    }
    
    private static boolean canTrim(final CharSequence text) {
        final int lastIx = text.length() - 1;
        return lastIx >= 0 && (Character.isWhitespace(text.charAt(0)) || Character.isWhitespace(text.charAt(lastIx)));
    }
    
    static {
        instance = new Indirect();
    }
}
