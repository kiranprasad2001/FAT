// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.function;

import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.regex.Pattern;

final class FunctionMetadataReader
{
    private static final String METADATA_FILE_NAME = "functionMetadata.txt";
    private static final String ELLIPSIS = "...";
    private static final Pattern TAB_DELIM_PATTERN;
    private static final Pattern SPACE_DELIM_PATTERN;
    private static final byte[] EMPTY_BYTE_ARRAY;
    private static final String[] DIGIT_ENDING_FUNCTION_NAMES;
    private static final Set<String> DIGIT_ENDING_FUNCTION_NAMES_SET;
    
    public static FunctionMetadataRegistry createRegistry() {
        final InputStream is = FunctionMetadataReader.class.getResourceAsStream("functionMetadata.txt");
        if (is == null) {
            throw new RuntimeException("resource 'functionMetadata.txt' not found");
        }
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        final FunctionDataBuilder fdb = new FunctionDataBuilder(400);
        try {
            while (true) {
                final String line = br.readLine();
                if (line == null) {
                    break;
                }
                if (line.length() < 1) {
                    continue;
                }
                if (line.charAt(0) == '#') {
                    continue;
                }
                final String trimLine = line.trim();
                if (trimLine.length() < 1) {
                    continue;
                }
                processLine(fdb, line);
            }
            br.close();
        }
        catch (IOException e2) {
            throw new RuntimeException(e2);
        }
        return fdb.build();
    }
    
    private static void processLine(final FunctionDataBuilder fdb, final String line) {
        final String[] parts = FunctionMetadataReader.TAB_DELIM_PATTERN.split(line, -2);
        if (parts.length != 8) {
            throw new RuntimeException("Bad line format '" + line + "' - expected 8 data fields");
        }
        final int functionIndex = parseInt(parts[0]);
        final String functionName = parts[1];
        final int minParams = parseInt(parts[2]);
        final int maxParams = parseInt(parts[3]);
        final byte returnClassCode = parseReturnTypeCode(parts[4]);
        final byte[] parameterClassCodes = parseOperandTypeCodes(parts[5]);
        final boolean hasNote = parts[7].length() > 0;
        validateFunctionName(functionName);
        fdb.add(functionIndex, functionName, minParams, maxParams, returnClassCode, parameterClassCodes, hasNote);
    }
    
    private static byte parseReturnTypeCode(final String code) {
        if (code.length() == 0) {
            return 0;
        }
        return parseOperandTypeCode(code);
    }
    
    private static byte[] parseOperandTypeCodes(final String codes) {
        if (codes.length() < 1) {
            return FunctionMetadataReader.EMPTY_BYTE_ARRAY;
        }
        if (isDash(codes)) {
            return FunctionMetadataReader.EMPTY_BYTE_ARRAY;
        }
        final String[] array = FunctionMetadataReader.SPACE_DELIM_PATTERN.split(codes);
        int nItems = array.length;
        if ("...".equals(array[nItems - 1])) {
            --nItems;
        }
        final byte[] result = new byte[nItems];
        for (int i = 0; i < nItems; ++i) {
            result[i] = parseOperandTypeCode(array[i]);
        }
        return result;
    }
    
    private static boolean isDash(final String codes) {
        if (codes.length() == 1) {
            switch (codes.charAt(0)) {
                case '-': {
                    return true;
                }
            }
        }
        return false;
    }
    
    private static byte parseOperandTypeCode(final String code) {
        if (code.length() != 1) {
            throw new RuntimeException("Bad operand type code format '" + code + "' expected single char");
        }
        switch (code.charAt(0)) {
            case 'V': {
                return 32;
            }
            case 'R': {
                return 0;
            }
            case 'A': {
                return 64;
            }
            default: {
                throw new IllegalArgumentException("Unexpected operand type code '" + code + "' (" + (int)code.charAt(0) + ")");
            }
        }
    }
    
    private static void validateFunctionName(final String functionName) {
        final int len = functionName.length();
        int ix = len - 1;
        if (!Character.isDigit(functionName.charAt(ix))) {
            return;
        }
        while (ix >= 0 && Character.isDigit(functionName.charAt(ix))) {
            --ix;
        }
        if (FunctionMetadataReader.DIGIT_ENDING_FUNCTION_NAMES_SET.contains(functionName)) {
            return;
        }
        throw new RuntimeException("Invalid function name '" + functionName + "' (is footnote number incorrectly appended)");
    }
    
    private static int parseInt(final String valStr) {
        try {
            return Integer.parseInt(valStr);
        }
        catch (NumberFormatException e) {
            throw new RuntimeException("Value '" + valStr + "' could not be parsed as an integer");
        }
    }
    
    static {
        TAB_DELIM_PATTERN = Pattern.compile("\t");
        SPACE_DELIM_PATTERN = Pattern.compile(" ");
        EMPTY_BYTE_ARRAY = new byte[0];
        DIGIT_ENDING_FUNCTION_NAMES = new String[] { "LOG10", "ATAN2", "DAYS360", "SUMXMY2", "SUMX2MY2", "SUMX2PY2" };
        DIGIT_ENDING_FUNCTION_NAMES_SET = new HashSet<String>(Arrays.asList(FunctionMetadataReader.DIGIT_ENDING_FUNCTION_NAMES));
    }
}
