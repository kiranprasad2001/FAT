// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.BoolEval;
import java.util.regex.Pattern;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.usermodel.DataFormatter;

public abstract class TextFunction implements Function
{
    protected static final DataFormatter formatter;
    protected static final String EMPTY_STRING = "";
    public static final Function CHAR;
    public static final Function LEN;
    public static final Function LOWER;
    public static final Function UPPER;
    public static final Function PROPER;
    public static final Function TRIM;
    public static final Function CLEAN;
    public static final Function MID;
    public static final Function LEFT;
    public static final Function RIGHT;
    public static final Function CONCATENATE;
    public static final Function EXACT;
    public static final Function TEXT;
    public static final Function FIND;
    public static final Function SEARCH;
    
    protected static final String evaluateStringArg(final ValueEval eval, final int srcRow, final int srcCol) throws EvaluationException {
        final ValueEval ve = OperandResolver.getSingleValue(eval, srcRow, srcCol);
        return OperandResolver.coerceValueToString(ve);
    }
    
    protected static final int evaluateIntArg(final ValueEval arg, final int srcCellRow, final int srcCellCol) throws EvaluationException {
        final ValueEval ve = OperandResolver.getSingleValue(arg, srcCellRow, srcCellCol);
        return OperandResolver.coerceValueToInt(ve);
    }
    
    protected static final double evaluateDoubleArg(final ValueEval arg, final int srcCellRow, final int srcCellCol) throws EvaluationException {
        final ValueEval ve = OperandResolver.getSingleValue(arg, srcCellRow, srcCellCol);
        return OperandResolver.coerceValueToDouble(ve);
    }
    
    @Override
    public final ValueEval evaluate(final ValueEval[] args, final int srcCellRow, final int srcCellCol) {
        try {
            return this.evaluateFunc(args, srcCellRow, srcCellCol);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }
    
    protected abstract ValueEval evaluateFunc(final ValueEval[] p0, final int p1, final int p2) throws EvaluationException;
    
    static {
        formatter = new DataFormatter();
        CHAR = new Fixed1ArgFunction() {
            @Override
            public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0) {
                int arg;
                try {
                    arg = TextFunction.evaluateIntArg(arg0, srcRowIndex, srcColumnIndex);
                    if (arg < 0 || arg >= 256) {
                        throw new EvaluationException(ErrorEval.VALUE_INVALID);
                    }
                }
                catch (EvaluationException e) {
                    return e.getErrorEval();
                }
                return new StringEval(String.valueOf((char)arg));
            }
        };
        LEN = new SingleArgTextFunc() {
            @Override
            protected ValueEval evaluate(final String arg) {
                return new NumberEval(arg.length());
            }
        };
        LOWER = new SingleArgTextFunc() {
            @Override
            protected ValueEval evaluate(final String arg) {
                return new StringEval(arg.toLowerCase());
            }
        };
        UPPER = new SingleArgTextFunc() {
            @Override
            protected ValueEval evaluate(final String arg) {
                return new StringEval(arg.toUpperCase());
            }
        };
        PROPER = new SingleArgTextFunc() {
            final Pattern nonAlphabeticPattern = Pattern.compile("\\P{IsL}");
            
            @Override
            protected ValueEval evaluate(final String text) {
                final StringBuilder sb = new StringBuilder();
                boolean shouldMakeUppercase = true;
                final String lowercaseText = text.toLowerCase();
                final String uppercaseText = text.toUpperCase();
                for (int i = 0; i < text.length(); ++i) {
                    if (shouldMakeUppercase) {
                        sb.append(uppercaseText.charAt(i));
                    }
                    else {
                        sb.append(lowercaseText.charAt(i));
                    }
                    shouldMakeUppercase = this.nonAlphabeticPattern.matcher(text.subSequence(i, i + 1)).matches();
                }
                return new StringEval(sb.toString());
            }
        };
        TRIM = new SingleArgTextFunc() {
            @Override
            protected ValueEval evaluate(final String arg) {
                return new StringEval(arg.trim());
            }
        };
        CLEAN = new SingleArgTextFunc() {
            @Override
            protected ValueEval evaluate(final String arg) {
                final StringBuilder result = new StringBuilder();
                for (int i = 0; i < arg.length(); ++i) {
                    final char c = arg.charAt(i);
                    if (this.isPrintable(c)) {
                        result.append(c);
                    }
                }
                return new StringEval(result.toString());
            }
            
            private boolean isPrintable(final char c) {
                final int charCode = c;
                return charCode >= 32;
            }
        };
        MID = new Fixed3ArgFunction() {
            @Override
            public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1, final ValueEval arg2) {
                String text;
                int startCharNum;
                int numChars;
                try {
                    text = TextFunction.evaluateStringArg(arg0, srcRowIndex, srcColumnIndex);
                    startCharNum = TextFunction.evaluateIntArg(arg1, srcRowIndex, srcColumnIndex);
                    numChars = TextFunction.evaluateIntArg(arg2, srcRowIndex, srcColumnIndex);
                }
                catch (EvaluationException e) {
                    return e.getErrorEval();
                }
                final int startIx = startCharNum - 1;
                if (startIx < 0) {
                    return ErrorEval.VALUE_INVALID;
                }
                if (numChars < 0) {
                    return ErrorEval.VALUE_INVALID;
                }
                final int len = text.length();
                if (numChars < 0 || startIx > len) {
                    return new StringEval("");
                }
                final int endIx = Math.min(startIx + numChars, len);
                final String result = text.substring(startIx, endIx);
                return new StringEval(result);
            }
        };
        LEFT = new LeftRight(true);
        RIGHT = new LeftRight(false);
        CONCATENATE = new Function() {
            @Override
            public ValueEval evaluate(final ValueEval[] args, final int srcRowIndex, final int srcColumnIndex) {
                final StringBuilder sb = new StringBuilder();
                for (int i = 0, iSize = args.length; i < iSize; ++i) {
                    try {
                        sb.append(TextFunction.evaluateStringArg(args[i], srcRowIndex, srcColumnIndex));
                    }
                    catch (EvaluationException e) {
                        return e.getErrorEval();
                    }
                }
                return new StringEval(sb.toString());
            }
        };
        EXACT = new Fixed2ArgFunction() {
            @Override
            public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1) {
                String s0;
                String s2;
                try {
                    s0 = TextFunction.evaluateStringArg(arg0, srcRowIndex, srcColumnIndex);
                    s2 = TextFunction.evaluateStringArg(arg1, srcRowIndex, srcColumnIndex);
                }
                catch (EvaluationException e) {
                    return e.getErrorEval();
                }
                return BoolEval.valueOf(s0.equals(s2));
            }
        };
        TEXT = new Fixed2ArgFunction() {
            @Override
            public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1) {
                double s0;
                String s2;
                try {
                    s0 = TextFunction.evaluateDoubleArg(arg0, srcRowIndex, srcColumnIndex);
                    s2 = TextFunction.evaluateStringArg(arg1, srcRowIndex, srcColumnIndex);
                }
                catch (EvaluationException e) {
                    return e.getErrorEval();
                }
                try {
                    final String formattedStr = TextFunction.formatter.formatRawCellContents(s0, -1, s2);
                    return new StringEval(formattedStr);
                }
                catch (Exception e2) {
                    return ErrorEval.VALUE_INVALID;
                }
            }
        };
        FIND = new SearchFind(true);
        SEARCH = new SearchFind(false);
    }
    
    private abstract static class SingleArgTextFunc extends Fixed1ArgFunction
    {
        protected SingleArgTextFunc() {
        }
        
        @Override
        public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0) {
            String arg;
            try {
                arg = TextFunction.evaluateStringArg(arg0, srcRowIndex, srcColumnIndex);
            }
            catch (EvaluationException e) {
                return e.getErrorEval();
            }
            return this.evaluate(arg);
        }
        
        protected abstract ValueEval evaluate(final String p0);
    }
    
    private static final class LeftRight extends Var1or2ArgFunction
    {
        private static final ValueEval DEFAULT_ARG1;
        private final boolean _isLeft;
        
        protected LeftRight(final boolean isLeft) {
            this._isLeft = isLeft;
        }
        
        @Override
        public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0) {
            return this.evaluate(srcRowIndex, srcColumnIndex, arg0, LeftRight.DEFAULT_ARG1);
        }
        
        @Override
        public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1) {
            String arg2;
            int index;
            try {
                arg2 = TextFunction.evaluateStringArg(arg0, srcRowIndex, srcColumnIndex);
                index = TextFunction.evaluateIntArg(arg1, srcRowIndex, srcColumnIndex);
            }
            catch (EvaluationException e) {
                return e.getErrorEval();
            }
            if (index < 0) {
                return ErrorEval.VALUE_INVALID;
            }
            String result;
            if (this._isLeft) {
                result = arg2.substring(0, Math.min(arg2.length(), index));
            }
            else {
                result = arg2.substring(Math.max(0, arg2.length() - index));
            }
            return new StringEval(result);
        }
        
        static {
            DEFAULT_ARG1 = new NumberEval(1.0);
        }
    }
    
    private static final class SearchFind extends Var2or3ArgFunction
    {
        private final boolean _isCaseSensitive;
        
        public SearchFind(final boolean isCaseSensitive) {
            this._isCaseSensitive = isCaseSensitive;
        }
        
        @Override
        public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1) {
            try {
                final String needle = TextFunction.evaluateStringArg(arg0, srcRowIndex, srcColumnIndex);
                final String haystack = TextFunction.evaluateStringArg(arg1, srcRowIndex, srcColumnIndex);
                return this.eval(haystack, needle, 0);
            }
            catch (EvaluationException e) {
                return e.getErrorEval();
            }
        }
        
        @Override
        public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1, final ValueEval arg2) {
            try {
                final String needle = TextFunction.evaluateStringArg(arg0, srcRowIndex, srcColumnIndex);
                final String haystack = TextFunction.evaluateStringArg(arg1, srcRowIndex, srcColumnIndex);
                final int startpos = TextFunction.evaluateIntArg(arg2, srcRowIndex, srcColumnIndex) - 1;
                if (startpos < 0) {
                    return ErrorEval.VALUE_INVALID;
                }
                return this.eval(haystack, needle, startpos);
            }
            catch (EvaluationException e) {
                return e.getErrorEval();
            }
        }
        
        private ValueEval eval(final String haystack, final String needle, final int startIndex) {
            int result;
            if (this._isCaseSensitive) {
                result = haystack.indexOf(needle, startIndex);
            }
            else {
                result = haystack.toUpperCase().indexOf(needle.toUpperCase(), startIndex);
            }
            if (result == -1) {
                return ErrorEval.VALUE_INVALID;
            }
            return new NumberEval(result + 1);
        }
    }
}
