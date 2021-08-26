// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula;

import java.util.Arrays;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianByteArrayInputStream;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.util.LittleEndianInput;

public class Formula
{
    private static final Formula EMPTY;
    private final byte[] _byteEncoding;
    private final int _encodedTokenLen;
    
    private Formula(final byte[] byteEncoding, final int encodedTokenLen) {
        this._byteEncoding = byteEncoding;
        this._encodedTokenLen = encodedTokenLen;
    }
    
    public static Formula read(final int encodedTokenLen, final LittleEndianInput in) {
        return read(encodedTokenLen, in, encodedTokenLen);
    }
    
    public static Formula read(final int encodedTokenLen, final LittleEndianInput in, final int totalEncodedLen) {
        final byte[] byteEncoding = new byte[totalEncodedLen];
        in.readFully(byteEncoding);
        return new Formula(byteEncoding, encodedTokenLen);
    }
    
    public Ptg[] getTokens() {
        final LittleEndianInput in = new LittleEndianByteArrayInputStream(this._byteEncoding);
        return Ptg.readTokens(this._encodedTokenLen, in);
    }
    
    public void serialize(final LittleEndianOutput out) {
        out.writeShort(this._encodedTokenLen);
        out.write(this._byteEncoding);
    }
    
    public void serializeTokens(final LittleEndianOutput out) {
        out.write(this._byteEncoding, 0, this._encodedTokenLen);
    }
    
    public void serializeArrayConstantData(final LittleEndianOutput out) {
        final int len = this._byteEncoding.length - this._encodedTokenLen;
        out.write(this._byteEncoding, this._encodedTokenLen, len);
    }
    
    public int getEncodedSize() {
        return 2 + this._byteEncoding.length;
    }
    
    public int getEncodedTokenSize() {
        return this._encodedTokenLen;
    }
    
    public static Formula create(final Ptg[] ptgs) {
        if (ptgs == null || ptgs.length < 1) {
            return Formula.EMPTY;
        }
        final int totalSize = Ptg.getEncodedSize(ptgs);
        final byte[] encodedData = new byte[totalSize];
        Ptg.serializePtgs(ptgs, encodedData, 0);
        final int encodedTokenLen = Ptg.getEncodedSizeWithoutArrayData(ptgs);
        return new Formula(encodedData, encodedTokenLen);
    }
    
    public static Ptg[] getTokens(final Formula formula) {
        if (formula == null) {
            return null;
        }
        return formula.getTokens();
    }
    
    public Formula copy() {
        return this;
    }
    
    public CellReference getExpReference() {
        final byte[] data = this._byteEncoding;
        if (data.length != 5) {
            return null;
        }
        switch (data[0]) {
            case 1: {
                break;
            }
            case 2: {
                break;
            }
            default: {
                return null;
            }
        }
        final int firstRow = LittleEndian.getUShort(data, 1);
        final int firstColumn = LittleEndian.getUShort(data, 3);
        return new CellReference(firstRow, firstColumn);
    }
    
    public boolean isSame(final Formula other) {
        return Arrays.equals(this._byteEncoding, other._byteEncoding);
    }
    
    static {
        EMPTY = new Formula(new byte[0], 0);
    }
}
