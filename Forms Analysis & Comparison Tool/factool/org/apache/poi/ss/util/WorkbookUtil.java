// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.util;

public class WorkbookUtil
{
    public static final String createSafeSheetName(final String nameProposal) {
        return createSafeSheetName(nameProposal, ' ');
    }
    
    public static final String createSafeSheetName(final String nameProposal, final char replaceChar) {
        if (nameProposal == null) {
            return "null";
        }
        if (nameProposal.length() < 1) {
            return "empty";
        }
        final int length = Math.min(31, nameProposal.length());
        final String shortenname = nameProposal.substring(0, length);
        final StringBuilder result = new StringBuilder(shortenname);
        for (int i = 0; i < length; ++i) {
            final char ch = result.charAt(i);
            switch (ch) {
                case '\0':
                case '\u0003':
                case '*':
                case '/':
                case ':':
                case '?':
                case '[':
                case '\\':
                case ']': {
                    result.setCharAt(i, replaceChar);
                    break;
                }
                case '\'': {
                    if (i == 0 || i == length - 1) {
                        result.setCharAt(i, replaceChar);
                        break;
                    }
                    break;
                }
            }
        }
        return result.toString();
    }
    
    public static void validateSheetName(final String sheetName) {
        if (sheetName == null) {
            throw new IllegalArgumentException("sheetName must not be null");
        }
        final int len = sheetName.length();
        if (len < 1 || len > 31) {
            throw new IllegalArgumentException("sheetName '" + sheetName + "' is invalid - character count MUST be greater than or equal to 1 and less than or equal to 31");
        }
        int i = 0;
        while (i < len) {
            final char ch = sheetName.charAt(i);
            switch (ch) {
                case '*':
                case '/':
                case ':':
                case '?':
                case '[':
                case '\\':
                case ']': {
                    throw new IllegalArgumentException("Invalid char (" + ch + ") found at index (" + i + ") in sheet name '" + sheetName + "'");
                }
                default: {
                    ++i;
                    continue;
                }
            }
        }
        if (sheetName.charAt(0) == '\'' || sheetName.charAt(len - 1) == '\'') {
            throw new IllegalArgumentException("Invalid sheet name '" + sheetName + "'. Sheet names must not begin or end with (').");
        }
    }
    
    public static void validateSheetState(final int state) {
        switch (state) {
            case 0: {
                break;
            }
            case 1: {
                break;
            }
            case 2: {
                break;
            }
            default: {
                throw new IllegalArgumentException("Ivalid sheet state : " + state + "\n" + "Sheet state must beone of the Workbook.SHEET_STATE_* constants");
            }
        }
    }
}
