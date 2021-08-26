// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

import java.util.HashMap;
import java.util.Map;

public enum FormulaError
{
    NULL(0, "#NULL!"), 
    DIV0(7, "#DIV/0!"), 
    VALUE(15, "#VALUE!"), 
    REF(23, "#REF!"), 
    NAME(29, "#NAME?"), 
    NUM(36, "#NUM!"), 
    NA(42, "#N/A"), 
    CIRCULAR_REF(-60, "~CIRCULAR~REF~"), 
    FUNCTION_NOT_IMPLEMENTED(-30, "~FUNCTION~NOT~IMPLEMENTED~");
    
    private final byte type;
    private final int longType;
    private final String repr;
    private static Map<String, FormulaError> smap;
    private static Map<Byte, FormulaError> bmap;
    private static Map<Integer, FormulaError> imap;
    
    private FormulaError(final int type, final String repr) {
        this.type = (byte)type;
        this.longType = type;
        this.repr = repr;
    }
    
    public byte getCode() {
        return this.type;
    }
    
    public int getLongCode() {
        return this.longType;
    }
    
    public String getString() {
        return this.repr;
    }
    
    public static final boolean isValidCode(final int errorCode) {
        for (final FormulaError error : values()) {
            if (error.getCode() == errorCode) {
                return true;
            }
            if (error.getLongCode() == errorCode) {
                return true;
            }
        }
        return false;
    }
    
    public static FormulaError forInt(final byte type) {
        final FormulaError err = FormulaError.bmap.get(type);
        if (err == null) {
            throw new IllegalArgumentException("Unknown error type: " + type);
        }
        return err;
    }
    
    public static FormulaError forInt(final int type) {
        FormulaError err = FormulaError.imap.get(type);
        if (err == null) {
            err = FormulaError.bmap.get((byte)type);
        }
        if (err == null) {
            throw new IllegalArgumentException("Unknown error type: " + type);
        }
        return err;
    }
    
    public static FormulaError forString(final String code) {
        final FormulaError err = FormulaError.smap.get(code);
        if (err == null) {
            throw new IllegalArgumentException("Unknown error code: " + code);
        }
        return err;
    }
    
    static {
        FormulaError.smap = new HashMap<String, FormulaError>();
        FormulaError.bmap = new HashMap<Byte, FormulaError>();
        FormulaError.imap = new HashMap<Integer, FormulaError>();
        for (final FormulaError error : values()) {
            FormulaError.bmap.put(error.getCode(), error);
            FormulaError.imap.put(error.getLongCode(), error);
            FormulaError.smap.put(error.getString(), error);
        }
    }
}
