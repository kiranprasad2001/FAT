// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.NumericValueEval;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.TwoDEval;

final class LookupUtils
{
    public static ValueVector createRowVector(final TwoDEval tableArray, final int relativeRowIndex) {
        return new RowVector(tableArray, relativeRowIndex);
    }
    
    public static ValueVector createColumnVector(final TwoDEval tableArray, final int relativeColumnIndex) {
        return new ColumnVector(tableArray, relativeColumnIndex);
    }
    
    public static ValueVector createVector(final TwoDEval ae) {
        if (ae.isColumn()) {
            return createColumnVector(ae, 0);
        }
        if (ae.isRow()) {
            return createRowVector(ae, 0);
        }
        return null;
    }
    
    public static ValueVector createVector(final RefEval re) {
        return new SheetVector(re);
    }
    
    public static int resolveRowOrColIndexArg(final ValueEval rowColIndexArg, final int srcCellRow, final int srcCellCol) throws EvaluationException {
        if (rowColIndexArg == null) {
            throw new IllegalArgumentException("argument must not be null");
        }
        ValueEval veRowColIndexArg;
        try {
            veRowColIndexArg = OperandResolver.getSingleValue(rowColIndexArg, srcCellRow, (short)srcCellCol);
        }
        catch (EvaluationException e) {
            throw EvaluationException.invalidRef();
        }
        if (veRowColIndexArg instanceof StringEval) {
            final StringEval se = (StringEval)veRowColIndexArg;
            final String strVal = se.getStringValue();
            final Double dVal = OperandResolver.parseDouble(strVal);
            if (dVal == null) {
                throw EvaluationException.invalidRef();
            }
        }
        final int oneBasedIndex = OperandResolver.coerceValueToInt(veRowColIndexArg);
        if (oneBasedIndex < 1) {
            throw EvaluationException.invalidValue();
        }
        return oneBasedIndex - 1;
    }
    
    public static TwoDEval resolveTableArrayArg(final ValueEval eval) throws EvaluationException {
        if (eval instanceof TwoDEval) {
            return (TwoDEval)eval;
        }
        if (eval instanceof RefEval) {
            final RefEval refEval = (RefEval)eval;
            return refEval.offset(0, 0, 0, 0);
        }
        throw EvaluationException.invalidValue();
    }
    
    public static boolean resolveRangeLookupArg(final ValueEval rangeLookupArg, final int srcCellRow, final int srcCellCol) throws EvaluationException {
        final ValueEval valEval = OperandResolver.getSingleValue(rangeLookupArg, srcCellRow, srcCellCol);
        if (valEval instanceof BlankEval) {
            return false;
        }
        if (valEval instanceof BoolEval) {
            final BoolEval boolEval = (BoolEval)valEval;
            return boolEval.getBooleanValue();
        }
        if (valEval instanceof StringEval) {
            final String stringValue = ((StringEval)valEval).getStringValue();
            if (stringValue.length() < 1) {
                throw EvaluationException.invalidValue();
            }
            final Boolean b = Countif.parseBoolean(stringValue);
            if (b != null) {
                return b;
            }
            throw EvaluationException.invalidValue();
        }
        else {
            if (valEval instanceof NumericValueEval) {
                final NumericValueEval nve = (NumericValueEval)valEval;
                return 0.0 != nve.getNumberValue();
            }
            throw new RuntimeException("Unexpected eval type (" + valEval.getClass().getName() + ")");
        }
    }
    
    public static int lookupIndexOfValue(final ValueEval lookupValue, final ValueVector vector, final boolean isRangeLookup) throws EvaluationException {
        final LookupValueComparer lookupComparer = createLookupComparer(lookupValue, isRangeLookup, false);
        int result;
        if (isRangeLookup) {
            result = performBinarySearch(vector, lookupComparer);
        }
        else {
            result = lookupIndexOfExactValue(lookupComparer, vector);
        }
        if (result < 0) {
            throw new EvaluationException(ErrorEval.NA);
        }
        return result;
    }
    
    private static int lookupIndexOfExactValue(final LookupValueComparer lookupComparer, final ValueVector vector) {
        for (int size = vector.getSize(), i = 0; i < size; ++i) {
            if (lookupComparer.compareTo(vector.getItem(i)).isEqual()) {
                return i;
            }
        }
        return -1;
    }
    
    private static int performBinarySearch(final ValueVector vector, final LookupValueComparer lookupComparer) {
        final BinarySearchIndexes bsi = new BinarySearchIndexes(vector.getSize());
        while (true) {
            int midIx = bsi.getMidIx();
            if (midIx < 0) {
                return bsi.getLowIx();
            }
            CompareResult cr = lookupComparer.compareTo(vector.getItem(midIx));
            if (cr.isTypeMismatch()) {
                final int newMidIx = handleMidValueTypeMismatch(lookupComparer, vector, bsi, midIx);
                if (newMidIx < 0) {
                    continue;
                }
                midIx = newMidIx;
                cr = lookupComparer.compareTo(vector.getItem(midIx));
            }
            if (cr.isEqual()) {
                return findLastIndexInRunOfEqualValues(lookupComparer, vector, midIx, bsi.getHighIx());
            }
            bsi.narrowSearch(midIx, cr.isLessThan());
        }
    }
    
    private static int handleMidValueTypeMismatch(final LookupValueComparer lookupComparer, final ValueVector vector, final BinarySearchIndexes bsi, final int midIx) {
        int newMid = midIx;
        final int highIx = bsi.getHighIx();
        while (++newMid != highIx) {
            final CompareResult cr = lookupComparer.compareTo(vector.getItem(newMid));
            if (cr.isLessThan() && newMid == highIx - 1) {
                bsi.narrowSearch(midIx, true);
                return -1;
            }
            if (cr.isTypeMismatch()) {
                continue;
            }
            if (cr.isEqual()) {
                return newMid;
            }
            bsi.narrowSearch(newMid, cr.isLessThan());
            return -1;
        }
        bsi.narrowSearch(midIx, true);
        return -1;
    }
    
    private static int findLastIndexInRunOfEqualValues(final LookupValueComparer lookupComparer, final ValueVector vector, final int firstFoundIndex, final int maxIx) {
        for (int i = firstFoundIndex + 1; i < maxIx; ++i) {
            if (!lookupComparer.compareTo(vector.getItem(i)).isEqual()) {
                return i - 1;
            }
        }
        return maxIx - 1;
    }
    
    public static LookupValueComparer createLookupComparer(final ValueEval lookupValue, final boolean matchExact, final boolean isMatchFunction) {
        if (lookupValue == BlankEval.instance) {
            return new NumberLookupComparer(NumberEval.ZERO);
        }
        if (lookupValue instanceof StringEval) {
            return new StringLookupComparer((StringEval)lookupValue, matchExact, isMatchFunction);
        }
        if (lookupValue instanceof NumberEval) {
            return new NumberLookupComparer((NumberEval)lookupValue);
        }
        if (lookupValue instanceof BoolEval) {
            return new BooleanLookupComparer((BoolEval)lookupValue);
        }
        throw new IllegalArgumentException("Bad lookup value type (" + lookupValue.getClass().getName() + ")");
    }
    
    private static final class RowVector implements ValueVector
    {
        private final TwoDEval _tableArray;
        private final int _size;
        private final int _rowIndex;
        
        public RowVector(final TwoDEval tableArray, final int rowIndex) {
            this._rowIndex = rowIndex;
            final int lastRowIx = tableArray.getHeight() - 1;
            if (rowIndex < 0 || rowIndex > lastRowIx) {
                throw new IllegalArgumentException("Specified row index (" + rowIndex + ") is outside the allowed range (0.." + lastRowIx + ")");
            }
            this._tableArray = tableArray;
            this._size = tableArray.getWidth();
        }
        
        @Override
        public ValueEval getItem(final int index) {
            if (index > this._size) {
                throw new ArrayIndexOutOfBoundsException("Specified index (" + index + ") is outside the allowed range (0.." + (this._size - 1) + ")");
            }
            return this._tableArray.getValue(this._rowIndex, index);
        }
        
        @Override
        public int getSize() {
            return this._size;
        }
    }
    
    private static final class ColumnVector implements ValueVector
    {
        private final TwoDEval _tableArray;
        private final int _size;
        private final int _columnIndex;
        
        public ColumnVector(final TwoDEval tableArray, final int columnIndex) {
            this._columnIndex = columnIndex;
            final int lastColIx = tableArray.getWidth() - 1;
            if (columnIndex < 0 || columnIndex > lastColIx) {
                throw new IllegalArgumentException("Specified column index (" + columnIndex + ") is outside the allowed range (0.." + lastColIx + ")");
            }
            this._tableArray = tableArray;
            this._size = this._tableArray.getHeight();
        }
        
        @Override
        public ValueEval getItem(final int index) {
            if (index > this._size) {
                throw new ArrayIndexOutOfBoundsException("Specified index (" + index + ") is outside the allowed range (0.." + (this._size - 1) + ")");
            }
            return this._tableArray.getValue(index, this._columnIndex);
        }
        
        @Override
        public int getSize() {
            return this._size;
        }
    }
    
    private static final class SheetVector implements ValueVector
    {
        private final RefEval _re;
        private final int _size;
        
        public SheetVector(final RefEval re) {
            this._size = re.getNumberOfSheets();
            this._re = re;
        }
        
        @Override
        public ValueEval getItem(final int index) {
            if (index >= this._size) {
                throw new ArrayIndexOutOfBoundsException("Specified index (" + index + ") is outside the allowed range (0.." + (this._size - 1) + ")");
            }
            final int sheetIndex = this._re.getFirstSheetIndex() + index;
            return this._re.getInnerValueEval(sheetIndex);
        }
        
        @Override
        public int getSize() {
            return this._size;
        }
    }
    
    public static final class CompareResult
    {
        private final boolean _isTypeMismatch;
        private final boolean _isLessThan;
        private final boolean _isEqual;
        private final boolean _isGreaterThan;
        public static final CompareResult TYPE_MISMATCH;
        public static final CompareResult LESS_THAN;
        public static final CompareResult EQUAL;
        public static final CompareResult GREATER_THAN;
        
        private CompareResult(final boolean isTypeMismatch, final int simpleCompareResult) {
            if (isTypeMismatch) {
                this._isTypeMismatch = true;
                this._isLessThan = false;
                this._isEqual = false;
                this._isGreaterThan = false;
            }
            else {
                this._isTypeMismatch = false;
                this._isLessThan = (simpleCompareResult < 0);
                this._isEqual = (simpleCompareResult == 0);
                this._isGreaterThan = (simpleCompareResult > 0);
            }
        }
        
        public static final CompareResult valueOf(final int simpleCompareResult) {
            if (simpleCompareResult < 0) {
                return CompareResult.LESS_THAN;
            }
            if (simpleCompareResult > 0) {
                return CompareResult.GREATER_THAN;
            }
            return CompareResult.EQUAL;
        }
        
        public static final CompareResult valueOf(final boolean matches) {
            if (matches) {
                return CompareResult.EQUAL;
            }
            return CompareResult.LESS_THAN;
        }
        
        public boolean isTypeMismatch() {
            return this._isTypeMismatch;
        }
        
        public boolean isLessThan() {
            return this._isLessThan;
        }
        
        public boolean isEqual() {
            return this._isEqual;
        }
        
        public boolean isGreaterThan() {
            return this._isGreaterThan;
        }
        
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer(64);
            sb.append(this.getClass().getName()).append(" [");
            sb.append(this.formatAsString());
            sb.append("]");
            return sb.toString();
        }
        
        private String formatAsString() {
            if (this._isTypeMismatch) {
                return "TYPE_MISMATCH";
            }
            if (this._isLessThan) {
                return "LESS_THAN";
            }
            if (this._isEqual) {
                return "EQUAL";
            }
            if (this._isGreaterThan) {
                return "GREATER_THAN";
            }
            return "??error??";
        }
        
        static {
            TYPE_MISMATCH = new CompareResult(true, 0);
            LESS_THAN = new CompareResult(false, -1);
            EQUAL = new CompareResult(false, 0);
            GREATER_THAN = new CompareResult(false, 1);
        }
    }
    
    private abstract static class LookupValueComparerBase implements LookupValueComparer
    {
        private final Class<? extends ValueEval> _targetClass;
        
        protected LookupValueComparerBase(final ValueEval targetValue) {
            if (targetValue == null) {
                throw new RuntimeException("targetValue cannot be null");
            }
            this._targetClass = targetValue.getClass();
        }
        
        @Override
        public final CompareResult compareTo(final ValueEval other) {
            if (other == null) {
                throw new RuntimeException("compare to value cannot be null");
            }
            if (this._targetClass != other.getClass()) {
                return CompareResult.TYPE_MISMATCH;
            }
            return this.compareSameType(other);
        }
        
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer(64);
            sb.append(this.getClass().getName()).append(" [");
            sb.append(this.getValueAsString());
            sb.append("]");
            return sb.toString();
        }
        
        protected abstract CompareResult compareSameType(final ValueEval p0);
        
        protected abstract String getValueAsString();
    }
    
    private static final class StringLookupComparer extends LookupValueComparerBase
    {
        private String _value;
        private final Pattern _wildCardPattern;
        private boolean _matchExact;
        private boolean _isMatchFunction;
        
        protected StringLookupComparer(final StringEval se, final boolean matchExact, final boolean isMatchFunction) {
            super(se);
            this._value = se.getStringValue();
            this._wildCardPattern = Countif.StringMatcher.getWildCardPattern(this._value);
            this._matchExact = matchExact;
            this._isMatchFunction = isMatchFunction;
        }
        
        @Override
        protected CompareResult compareSameType(final ValueEval other) {
            final StringEval se = (StringEval)other;
            final String stringValue = se.getStringValue();
            if (this._wildCardPattern != null) {
                final Matcher matcher = this._wildCardPattern.matcher(stringValue);
                final boolean matches = matcher.matches();
                if (this._isMatchFunction || !this._matchExact) {
                    return CompareResult.valueOf(matches);
                }
            }
            return CompareResult.valueOf(this._value.compareToIgnoreCase(stringValue));
        }
        
        @Override
        protected String getValueAsString() {
            return this._value;
        }
    }
    
    private static final class NumberLookupComparer extends LookupValueComparerBase
    {
        private double _value;
        
        protected NumberLookupComparer(final NumberEval ne) {
            super(ne);
            this._value = ne.getNumberValue();
        }
        
        @Override
        protected CompareResult compareSameType(final ValueEval other) {
            final NumberEval ne = (NumberEval)other;
            return CompareResult.valueOf(Double.compare(this._value, ne.getNumberValue()));
        }
        
        @Override
        protected String getValueAsString() {
            return String.valueOf(this._value);
        }
    }
    
    private static final class BooleanLookupComparer extends LookupValueComparerBase
    {
        private boolean _value;
        
        protected BooleanLookupComparer(final BoolEval be) {
            super(be);
            this._value = be.getBooleanValue();
        }
        
        @Override
        protected CompareResult compareSameType(final ValueEval other) {
            final BoolEval be = (BoolEval)other;
            final boolean otherVal = be.getBooleanValue();
            if (this._value == otherVal) {
                return CompareResult.EQUAL;
            }
            if (this._value) {
                return CompareResult.GREATER_THAN;
            }
            return CompareResult.LESS_THAN;
        }
        
        @Override
        protected String getValueAsString() {
            return String.valueOf(this._value);
        }
    }
    
    private static final class BinarySearchIndexes
    {
        private int _lowIx;
        private int _highIx;
        
        public BinarySearchIndexes(final int highIx) {
            this._lowIx = -1;
            this._highIx = highIx;
        }
        
        public int getMidIx() {
            final int ixDiff = this._highIx - this._lowIx;
            if (ixDiff < 2) {
                return -1;
            }
            return this._lowIx + ixDiff / 2;
        }
        
        public int getLowIx() {
            return this._lowIx;
        }
        
        public int getHighIx() {
            return this._highIx;
        }
        
        public void narrowSearch(final int midIx, final boolean isLessThan) {
            if (isLessThan) {
                this._highIx = midIx;
            }
            else {
                this._lowIx = midIx;
            }
        }
    }
    
    public interface LookupValueComparer
    {
        CompareResult compareTo(final ValueEval p0);
    }
    
    public interface ValueVector
    {
        ValueEval getItem(final int p0);
        
        int getSize();
    }
}
