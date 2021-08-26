// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.format;

import java.util.HashMap;
import java.util.Map;

public abstract class CellFormatCondition
{
    private static final int LT = 0;
    private static final int LE = 1;
    private static final int GT = 2;
    private static final int GE = 3;
    private static final int EQ = 4;
    private static final int NE = 5;
    private static final Map<String, Integer> TESTS;
    
    public static CellFormatCondition getInstance(final String opString, final String constStr) {
        if (!CellFormatCondition.TESTS.containsKey(opString)) {
            throw new IllegalArgumentException("Unknown test: " + opString);
        }
        final int test = CellFormatCondition.TESTS.get(opString);
        final double c = Double.parseDouble(constStr);
        switch (test) {
            case 0: {
                return new CellFormatCondition() {
                    @Override
                    public boolean pass(final double value) {
                        return value < c;
                    }
                };
            }
            case 1: {
                return new CellFormatCondition() {
                    @Override
                    public boolean pass(final double value) {
                        return value <= c;
                    }
                };
            }
            case 2: {
                return new CellFormatCondition() {
                    @Override
                    public boolean pass(final double value) {
                        return value > c;
                    }
                };
            }
            case 3: {
                return new CellFormatCondition() {
                    @Override
                    public boolean pass(final double value) {
                        return value >= c;
                    }
                };
            }
            case 4: {
                return new CellFormatCondition() {
                    @Override
                    public boolean pass(final double value) {
                        return value == c;
                    }
                };
            }
            case 5: {
                return new CellFormatCondition() {
                    @Override
                    public boolean pass(final double value) {
                        return value != c;
                    }
                };
            }
            default: {
                throw new IllegalArgumentException("Cannot create for test number " + test + "(\"" + opString + "\")");
            }
        }
    }
    
    public abstract boolean pass(final double p0);
    
    static {
        (TESTS = new HashMap<String, Integer>()).put("<", 0);
        CellFormatCondition.TESTS.put("<=", 1);
        CellFormatCondition.TESTS.put(">", 2);
        CellFormatCondition.TESTS.put(">=", 3);
        CellFormatCondition.TESTS.put("=", 4);
        CellFormatCondition.TESTS.put("==", 4);
        CellFormatCondition.TESTS.put("!=", 5);
        CellFormatCondition.TESTS.put("<>", 5);
    }
}
