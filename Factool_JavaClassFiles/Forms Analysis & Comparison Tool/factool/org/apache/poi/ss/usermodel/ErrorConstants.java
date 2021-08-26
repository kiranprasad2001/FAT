// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

public class ErrorConstants
{
    public static final int ERROR_NULL = 0;
    public static final int ERROR_DIV_0 = 7;
    public static final int ERROR_VALUE = 15;
    public static final int ERROR_REF = 23;
    public static final int ERROR_NAME = 29;
    public static final int ERROR_NUM = 36;
    public static final int ERROR_NA = 42;
    
    protected ErrorConstants() {
    }
    
    public static final String getText(final int errorCode) {
        switch (errorCode) {
            case 0: {
                return "#NULL!";
            }
            case 7: {
                return "#DIV/0!";
            }
            case 15: {
                return "#VALUE!";
            }
            case 23: {
                return "#REF!";
            }
            case 29: {
                return "#NAME?";
            }
            case 36: {
                return "#NUM!";
            }
            case 42: {
                return "#N/A";
            }
            default: {
                throw new IllegalArgumentException("Bad error code (" + errorCode + ")");
            }
        }
    }
    
    public static final boolean isValidCode(final int errorCode) {
        switch (errorCode) {
            case 0:
            case 7:
            case 15:
            case 23:
            case 29:
            case 36:
            case 42: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
}
