// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

public interface DataValidationConstraint
{
    int getValidationType();
    
    int getOperator();
    
    void setOperator(final int p0);
    
    String[] getExplicitListValues();
    
    void setExplicitListValues(final String[] p0);
    
    String getFormula1();
    
    void setFormula1(final String p0);
    
    String getFormula2();
    
    void setFormula2(final String p0);
    
    public static final class ValidationType
    {
        public static final int ANY = 0;
        public static final int INTEGER = 1;
        public static final int DECIMAL = 2;
        public static final int LIST = 3;
        public static final int DATE = 4;
        public static final int TIME = 5;
        public static final int TEXT_LENGTH = 6;
        public static final int FORMULA = 7;
        
        private ValidationType() {
        }
    }
    
    public static final class OperatorType
    {
        public static final int BETWEEN = 0;
        public static final int NOT_BETWEEN = 1;
        public static final int EQUAL = 2;
        public static final int NOT_EQUAL = 3;
        public static final int GREATER_THAN = 4;
        public static final int LESS_THAN = 5;
        public static final int GREATER_OR_EQUAL = 6;
        public static final int LESS_OR_EQUAL = 7;
        public static final int IGNORED = 0;
        
        private OperatorType() {
        }
        
        public static void validateSecondArg(final int comparisonOperator, final String paramValue) {
            switch (comparisonOperator) {
                case 0:
                case 1: {
                    if (paramValue == null) {
                        throw new IllegalArgumentException("expr2 must be supplied for 'between' comparisons");
                    }
                    break;
                }
            }
        }
    }
}
