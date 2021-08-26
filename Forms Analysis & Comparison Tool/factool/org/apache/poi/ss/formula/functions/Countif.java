// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import java.util.regex.Pattern;
import org.apache.poi.ss.usermodel.ErrorConstants;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.ThreeDEval;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class Countif extends Fixed2ArgFunction
{
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1) {
        final CountUtils.I_MatchPredicate mp = createCriteriaPredicate(arg1, srcRowIndex, srcColumnIndex);
        if (mp == null) {
            return NumberEval.ZERO;
        }
        final double result = this.countMatchingCellsInArea(arg0, mp);
        return new NumberEval(result);
    }
    
    private double countMatchingCellsInArea(final ValueEval rangeArg, final CountUtils.I_MatchPredicate criteriaPredicate) {
        if (rangeArg instanceof RefEval) {
            return CountUtils.countMatchingCellsInRef((RefEval)rangeArg, criteriaPredicate);
        }
        if (rangeArg instanceof ThreeDEval) {
            return CountUtils.countMatchingCellsInArea((ThreeDEval)rangeArg, criteriaPredicate);
        }
        throw new IllegalArgumentException("Bad range arg type (" + rangeArg.getClass().getName() + ")");
    }
    
    static CountUtils.I_MatchPredicate createCriteriaPredicate(final ValueEval arg, final int srcRowIndex, final int srcColumnIndex) {
        final ValueEval evaluatedCriteriaArg = evaluateCriteriaArg(arg, srcRowIndex, srcColumnIndex);
        if (evaluatedCriteriaArg instanceof NumberEval) {
            return new NumberMatcher(((NumberEval)evaluatedCriteriaArg).getNumberValue(), CmpOp.OP_NONE);
        }
        if (evaluatedCriteriaArg instanceof BoolEval) {
            return new BooleanMatcher(((BoolEval)evaluatedCriteriaArg).getBooleanValue(), CmpOp.OP_NONE);
        }
        if (evaluatedCriteriaArg instanceof StringEval) {
            return createGeneralMatchPredicate((StringEval)evaluatedCriteriaArg);
        }
        if (evaluatedCriteriaArg instanceof ErrorEval) {
            return new ErrorMatcher(((ErrorEval)evaluatedCriteriaArg).getErrorCode(), CmpOp.OP_NONE);
        }
        if (evaluatedCriteriaArg == BlankEval.instance) {
            return null;
        }
        throw new RuntimeException("Unexpected type for criteria (" + evaluatedCriteriaArg.getClass().getName() + ")");
    }
    
    private static ValueEval evaluateCriteriaArg(final ValueEval arg, final int srcRowIndex, final int srcColumnIndex) {
        try {
            return OperandResolver.getSingleValue(arg, srcRowIndex, (short)srcColumnIndex);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }
    
    private static CountUtils.I_MatchPredicate createGeneralMatchPredicate(final StringEval stringEval) {
        String value = stringEval.getStringValue();
        final CmpOp operator = CmpOp.getOperator(value);
        value = value.substring(operator.getLength());
        final Boolean booleanVal = parseBoolean(value);
        if (booleanVal != null) {
            return new BooleanMatcher(booleanVal, operator);
        }
        final Double doubleVal = OperandResolver.parseDouble(value);
        if (doubleVal != null) {
            return new NumberMatcher(doubleVal, operator);
        }
        final ErrorEval ee = parseError(value);
        if (ee != null) {
            return new ErrorMatcher(ee.getErrorCode(), operator);
        }
        return new StringMatcher(value, operator);
    }
    
    private static ErrorEval parseError(final String value) {
        if (value.length() < 4 || value.charAt(0) != '#') {
            return null;
        }
        if (value.equals("#NULL!")) {
            return ErrorEval.NULL_INTERSECTION;
        }
        if (value.equals("#DIV/0!")) {
            return ErrorEval.DIV_ZERO;
        }
        if (value.equals("#VALUE!")) {
            return ErrorEval.VALUE_INVALID;
        }
        if (value.equals("#REF!")) {
            return ErrorEval.REF_INVALID;
        }
        if (value.equals("#NAME?")) {
            return ErrorEval.NAME_INVALID;
        }
        if (value.equals("#NUM!")) {
            return ErrorEval.NUM_ERROR;
        }
        if (value.equals("#N/A")) {
            return ErrorEval.NA;
        }
        return null;
    }
    
    static Boolean parseBoolean(final String strRep) {
        if (strRep.length() < 1) {
            return null;
        }
        switch (strRep.charAt(0)) {
            case 'T':
            case 't': {
                if ("TRUE".equalsIgnoreCase(strRep)) {
                    return Boolean.TRUE;
                }
                break;
            }
            case 'F':
            case 'f': {
                if ("FALSE".equalsIgnoreCase(strRep)) {
                    return Boolean.FALSE;
                }
                break;
            }
        }
        return null;
    }
    
    private static final class CmpOp
    {
        public static final int NONE = 0;
        public static final int EQ = 1;
        public static final int NE = 2;
        public static final int LE = 3;
        public static final int LT = 4;
        public static final int GT = 5;
        public static final int GE = 6;
        public static final CmpOp OP_NONE;
        public static final CmpOp OP_EQ;
        public static final CmpOp OP_NE;
        public static final CmpOp OP_LE;
        public static final CmpOp OP_LT;
        public static final CmpOp OP_GT;
        public static final CmpOp OP_GE;
        private final String _representation;
        private final int _code;
        
        private static CmpOp op(final String rep, final int code) {
            return new CmpOp(rep, code);
        }
        
        private CmpOp(final String representation, final int code) {
            this._representation = representation;
            this._code = code;
        }
        
        public int getLength() {
            return this._representation.length();
        }
        
        public int getCode() {
            return this._code;
        }
        
        public static CmpOp getOperator(final String value) {
            final int len = value.length();
            if (len < 1) {
                return CmpOp.OP_NONE;
            }
            final char firstChar = value.charAt(0);
            switch (firstChar) {
                case '=': {
                    return CmpOp.OP_EQ;
                }
                case '>': {
                    if (len > 1) {
                        switch (value.charAt(1)) {
                            case '=': {
                                return CmpOp.OP_GE;
                            }
                        }
                    }
                    return CmpOp.OP_GT;
                }
                case '<': {
                    if (len > 1) {
                        switch (value.charAt(1)) {
                            case '=': {
                                return CmpOp.OP_LE;
                            }
                            case '>': {
                                return CmpOp.OP_NE;
                            }
                        }
                    }
                    return CmpOp.OP_LT;
                }
                default: {
                    return CmpOp.OP_NONE;
                }
            }
        }
        
        public boolean evaluate(final boolean cmpResult) {
            switch (this._code) {
                case 0:
                case 1: {
                    return cmpResult;
                }
                case 2: {
                    return !cmpResult;
                }
                default: {
                    throw new RuntimeException("Cannot call boolean evaluate on non-equality operator '" + this._representation + "'");
                }
            }
        }
        
        public boolean evaluate(final int cmpResult) {
            switch (this._code) {
                case 0:
                case 1: {
                    return cmpResult == 0;
                }
                case 2: {
                    return cmpResult != 0;
                }
                case 4: {
                    return cmpResult < 0;
                }
                case 3: {
                    return cmpResult <= 0;
                }
                case 5: {
                    return cmpResult > 0;
                }
                case 6: {
                    return cmpResult >= 0;
                }
                default: {
                    throw new RuntimeException("Cannot call boolean evaluate on non-equality operator '" + this._representation + "'");
                }
            }
        }
        
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer(64);
            sb.append(this.getClass().getName());
            sb.append(" [").append(this._representation).append("]");
            return sb.toString();
        }
        
        public String getRepresentation() {
            return this._representation;
        }
        
        static {
            OP_NONE = op("", 0);
            OP_EQ = op("=", 1);
            OP_NE = op("<>", 2);
            OP_LE = op("<=", 3);
            OP_LT = op("<", 4);
            OP_GT = op(">", 5);
            OP_GE = op(">=", 6);
        }
    }
    
    private abstract static class MatcherBase implements CountUtils.I_MatchPredicate
    {
        private final CmpOp _operator;
        
        MatcherBase(final CmpOp operator) {
            this._operator = operator;
        }
        
        protected final int getCode() {
            return this._operator.getCode();
        }
        
        protected final boolean evaluate(final int cmpResult) {
            return this._operator.evaluate(cmpResult);
        }
        
        protected final boolean evaluate(final boolean cmpResult) {
            return this._operator.evaluate(cmpResult);
        }
        
        @Override
        public final String toString() {
            final StringBuffer sb = new StringBuffer(64);
            sb.append(this.getClass().getName()).append(" [");
            sb.append(this._operator.getRepresentation());
            sb.append(this.getValueText());
            sb.append("]");
            return sb.toString();
        }
        
        protected abstract String getValueText();
    }
    
    private static final class NumberMatcher extends MatcherBase
    {
        private final double _value;
        
        public NumberMatcher(final double value, final CmpOp operator) {
            super(operator);
            this._value = value;
        }
        
        @Override
        protected String getValueText() {
            return String.valueOf(this._value);
        }
        
        @Override
        public boolean matches(final ValueEval x) {
            if (x instanceof StringEval) {
                switch (this.getCode()) {
                    case 0:
                    case 1: {
                        final StringEval se = (StringEval)x;
                        final Double val = OperandResolver.parseDouble(se.getStringValue());
                        return val != null && this._value == val;
                    }
                    case 2: {
                        return true;
                    }
                    default: {
                        return false;
                    }
                }
            }
            else {
                if (x instanceof NumberEval) {
                    final NumberEval ne = (NumberEval)x;
                    final double testValue = ne.getNumberValue();
                    return this.evaluate(Double.compare(testValue, this._value));
                }
                if (!(x instanceof BlankEval)) {
                    return false;
                }
                switch (this.getCode()) {
                    case 2: {
                        return true;
                    }
                    default: {
                        return false;
                    }
                }
            }
        }
    }
    
    private static final class BooleanMatcher extends MatcherBase
    {
        private final int _value;
        
        public BooleanMatcher(final boolean value, final CmpOp operator) {
            super(operator);
            this._value = boolToInt(value);
        }
        
        @Override
        protected String getValueText() {
            return (this._value == 1) ? "TRUE" : "FALSE";
        }
        
        private static int boolToInt(final boolean value) {
            return value ? 1 : 0;
        }
        
        @Override
        public boolean matches(final ValueEval x) {
            if (x instanceof StringEval) {
                return false;
            }
            if (x instanceof BoolEval) {
                final BoolEval be = (BoolEval)x;
                final int testValue = boolToInt(be.getBooleanValue());
                return this.evaluate(testValue - this._value);
            }
            if (x instanceof BlankEval) {
                switch (this.getCode()) {
                    case 2: {
                        return true;
                    }
                    default: {
                        return false;
                    }
                }
            }
            else {
                if (!(x instanceof NumberEval)) {
                    return false;
                }
                switch (this.getCode()) {
                    case 2: {
                        return true;
                    }
                    default: {
                        return false;
                    }
                }
            }
        }
    }
    
    private static final class ErrorMatcher extends MatcherBase
    {
        private final int _value;
        
        public ErrorMatcher(final int errorCode, final CmpOp operator) {
            super(operator);
            this._value = errorCode;
        }
        
        @Override
        protected String getValueText() {
            return ErrorConstants.getText(this._value);
        }
        
        @Override
        public boolean matches(final ValueEval x) {
            if (x instanceof ErrorEval) {
                final int testValue = ((ErrorEval)x).getErrorCode();
                return this.evaluate(testValue - this._value);
            }
            return false;
        }
    }
    
    public static final class StringMatcher extends MatcherBase
    {
        private final String _value;
        private final Pattern _pattern;
        
        public StringMatcher(final String value, final CmpOp operator) {
            super(operator);
            this._value = value;
            switch (operator.getCode()) {
                case 0:
                case 1:
                case 2: {
                    this._pattern = getWildCardPattern(value);
                    break;
                }
                default: {
                    this._pattern = null;
                    break;
                }
            }
        }
        
        @Override
        protected String getValueText() {
            if (this._pattern == null) {
                return this._value;
            }
            return this._pattern.pattern();
        }
        
        @Override
        public boolean matches(final ValueEval x) {
            if (x instanceof BlankEval) {
                switch (this.getCode()) {
                    case 0:
                    case 1: {
                        return this._value.length() == 0;
                    }
                    case 2: {
                        return this._value.length() != 0;
                    }
                    default: {
                        return false;
                    }
                }
            }
            else {
                if (!(x instanceof StringEval)) {
                    return false;
                }
                final String testedValue = ((StringEval)x).getStringValue();
                if (testedValue.length() < 1 && this._value.length() < 1) {
                    switch (this.getCode()) {
                        case 0: {
                            return true;
                        }
                        case 1: {
                            return false;
                        }
                        case 2: {
                            return true;
                        }
                        default: {
                            return false;
                        }
                    }
                }
                else {
                    if (this._pattern != null) {
                        return this.evaluate(this._pattern.matcher(testedValue).matches());
                    }
                    return this.evaluate(testedValue.compareToIgnoreCase(this._value));
                }
            }
        }
        
        public static Pattern getWildCardPattern(final String value) {
            final int len = value.length();
            final StringBuffer sb = new StringBuffer(len);
            boolean hasWildCard = false;
            for (int i = 0; i < len; ++i) {
                char ch = value.charAt(i);
                switch (ch) {
                    case '?': {
                        hasWildCard = true;
                        sb.append('.');
                        break;
                    }
                    case '*': {
                        hasWildCard = true;
                        sb.append(".*");
                        break;
                    }
                    case '~': {
                        if (i + 1 < len) {
                            ch = value.charAt(i + 1);
                            switch (ch) {
                                case '*':
                                case '?': {
                                    hasWildCard = true;
                                    sb.append('[').append(ch).append(']');
                                    ++i;
                                    continue;
                                }
                            }
                        }
                        sb.append('~');
                        break;
                    }
                    case '$':
                    case '(':
                    case ')':
                    case '.':
                    case '[':
                    case ']':
                    case '^': {
                        sb.append("\\").append(ch);
                        break;
                    }
                    default: {
                        sb.append(ch);
                        break;
                    }
                }
            }
            if (hasWildCard) {
                return Pattern.compile(sb.toString(), 2);
            }
            return null;
        }
    }
}
