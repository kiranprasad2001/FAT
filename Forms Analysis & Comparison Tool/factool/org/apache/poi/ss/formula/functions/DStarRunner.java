// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.util.NumberComparer;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.apache.poi.ss.formula.eval.StringValueEval;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.NumericValueEval;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.TwoDEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class DStarRunner implements Function3Arg
{
    private IDStarAlgorithm algorithm;
    
    public DStarRunner(final IDStarAlgorithm algorithm) {
        this.algorithm = algorithm;
    }
    
    @Override
    public final ValueEval evaluate(final ValueEval[] args, final int srcRowIndex, final int srcColumnIndex) {
        if (args.length == 3) {
            return this.evaluate(srcRowIndex, srcColumnIndex, args[0], args[1], args[2]);
        }
        return ErrorEval.VALUE_INVALID;
    }
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval database, final ValueEval filterColumn, final ValueEval conditionDatabase) {
        if (!(database instanceof TwoDEval) || !(conditionDatabase instanceof TwoDEval)) {
            return ErrorEval.VALUE_INVALID;
        }
        final TwoDEval db = (TwoDEval)database;
        final TwoDEval cdb = (TwoDEval)conditionDatabase;
        int fc;
        try {
            fc = getColumnForName(filterColumn, db);
        }
        catch (EvaluationException e2) {
            return ErrorEval.VALUE_INVALID;
        }
        if (fc == -1) {
            return ErrorEval.VALUE_INVALID;
        }
        this.algorithm.reset();
        for (int row = 1; row < db.getHeight(); ++row) {
            boolean matches = true;
            try {
                matches = fullfillsConditions(db, row, cdb);
            }
            catch (EvaluationException e) {
                return ErrorEval.VALUE_INVALID;
            }
            if (matches) {
                try {
                    final ValueEval currentValueEval = solveReference(db.getValue(row, fc));
                    final boolean shouldContinue = this.algorithm.processMatch(currentValueEval);
                    if (!shouldContinue) {
                        break;
                    }
                }
                catch (EvaluationException e) {
                    return e.getErrorEval();
                }
            }
        }
        return this.algorithm.getResult();
    }
    
    private static ValueEval solveReference(final ValueEval field) throws EvaluationException {
        if (!(field instanceof RefEval)) {
            return field;
        }
        final RefEval refEval = (RefEval)field;
        if (refEval.getNumberOfSheets() > 1) {
            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        }
        return solveReference(refEval.getInnerValueEval(refEval.getFirstSheetIndex()));
    }
    
    private static int getColumnForTag(final ValueEval nameValueEval, final TwoDEval db) throws EvaluationException {
        int resultColumn = -1;
        if (nameValueEval instanceof NumericValueEval) {
            final double doubleResultColumn = ((NumericValueEval)nameValueEval).getNumberValue();
            resultColumn = (int)doubleResultColumn;
            if (doubleResultColumn - resultColumn != 0.0) {
                throw new EvaluationException(ErrorEval.VALUE_INVALID);
            }
            --resultColumn;
        }
        else {
            resultColumn = getColumnForName(nameValueEval, db);
        }
        return resultColumn;
    }
    
    private static int getColumnForName(final ValueEval nameValueEval, final TwoDEval db) throws EvaluationException {
        final String name = getStringFromValueEval(nameValueEval);
        return getColumnForString(db, name);
    }
    
    private static int getColumnForString(final TwoDEval db, final String name) throws EvaluationException {
        int resultColumn = -1;
        for (int column = 0; column < db.getWidth(); ++column) {
            final ValueEval columnNameValueEval = db.getValue(0, column);
            final String columnName = getStringFromValueEval(columnNameValueEval);
            if (name.equals(columnName)) {
                resultColumn = column;
                break;
            }
        }
        return resultColumn;
    }
    
    private static boolean fullfillsConditions(final TwoDEval db, final int row, final TwoDEval cdb) throws EvaluationException {
        for (int conditionRow = 1; conditionRow < cdb.getHeight(); ++conditionRow) {
            boolean matches = true;
            for (int column = 0; column < cdb.getWidth(); ++column) {
                boolean columnCondition = true;
                ValueEval condition = null;
                try {
                    condition = solveReference(cdb.getValue(conditionRow, column));
                }
                catch (RuntimeException e) {
                    columnCondition = false;
                }
                if (!(condition instanceof BlankEval)) {
                    ValueEval targetHeader = solveReference(cdb.getValue(0, column));
                    targetHeader = solveReference(targetHeader);
                    if (!(targetHeader instanceof StringValueEval)) {
                        columnCondition = false;
                    }
                    else if (getColumnForName(targetHeader, db) == -1) {
                        columnCondition = false;
                    }
                    if (!columnCondition) {
                        throw new NotImplementedException("D* function with formula conditions");
                    }
                    final ValueEval target = db.getValue(row, getColumnForName(targetHeader, db));
                    final String conditionString = getStringFromValueEval(condition);
                    if (!testNormalCondition(target, conditionString)) {
                        matches = false;
                        break;
                    }
                }
            }
            if (matches) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean testNormalCondition(final ValueEval value, final String condition) throws EvaluationException {
        if (condition.startsWith("<")) {
            String number = condition.substring(1);
            if (number.startsWith("=")) {
                number = number.substring(1);
                return testNumericCondition(value, operator.smallerEqualThan, number);
            }
            return testNumericCondition(value, operator.smallerThan, number);
        }
        else if (condition.startsWith(">")) {
            String number = condition.substring(1);
            if (number.startsWith("=")) {
                number = number.substring(1);
                return testNumericCondition(value, operator.largerEqualThan, number);
            }
            return testNumericCondition(value, operator.largerThan, number);
        }
        else {
            if (!condition.startsWith("=")) {
                final String valueString = getStringFromValueEval(value);
                return valueString.startsWith(condition);
            }
            final String stringOrNumber = condition.substring(1);
            boolean itsANumber = false;
            try {
                Integer.parseInt(stringOrNumber);
                itsANumber = true;
            }
            catch (NumberFormatException e) {
                try {
                    Double.parseDouble(stringOrNumber);
                    itsANumber = true;
                }
                catch (NumberFormatException e2) {
                    itsANumber = false;
                }
            }
            if (itsANumber) {
                return testNumericCondition(value, operator.equal, stringOrNumber);
            }
            final String valueString2 = getStringFromValueEval(value);
            return stringOrNumber.equals(valueString2);
        }
    }
    
    private static boolean testNumericCondition(final ValueEval valueEval, final operator op, final String condition) throws EvaluationException {
        if (!(valueEval instanceof NumericValueEval)) {
            return false;
        }
        final double value = ((NumericValueEval)valueEval).getNumberValue();
        double conditionValue = 0.0;
        try {
            final int intValue = Integer.parseInt(condition);
            conditionValue = intValue;
        }
        catch (NumberFormatException e) {
            try {
                conditionValue = Double.parseDouble(condition);
            }
            catch (NumberFormatException e2) {
                throw new EvaluationException(ErrorEval.VALUE_INVALID);
            }
        }
        final int result = NumberComparer.compare(value, conditionValue);
        switch (op) {
            case largerThan: {
                return result > 0;
            }
            case largerEqualThan: {
                return result >= 0;
            }
            case smallerThan: {
                return result < 0;
            }
            case smallerEqualThan: {
                return result <= 0;
            }
            case equal: {
                return result == 0;
            }
            default: {
                return false;
            }
        }
    }
    
    private static String getStringFromValueEval(ValueEval value) throws EvaluationException {
        value = solveReference(value);
        if (value instanceof BlankEval) {
            return "";
        }
        if (!(value instanceof StringValueEval)) {
            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        }
        return ((StringValueEval)value).getStringValue();
    }
    
    private enum operator
    {
        largerThan, 
        largerEqualThan, 
        smallerThan, 
        smallerEqualThan, 
        equal;
    }
}
