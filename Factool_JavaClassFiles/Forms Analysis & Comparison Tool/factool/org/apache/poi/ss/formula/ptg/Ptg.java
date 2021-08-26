// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.ptg;

import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianByteArrayOutputStream;
import java.util.List;
import java.util.ArrayList;
import org.apache.poi.util.LittleEndianInput;

public abstract class Ptg
{
    public static final Ptg[] EMPTY_PTG_ARRAY;
    public static final byte CLASS_REF = 0;
    public static final byte CLASS_VALUE = 32;
    public static final byte CLASS_ARRAY = 64;
    private byte ptgClass;
    
    public Ptg() {
        this.ptgClass = 0;
    }
    
    public static Ptg[] readTokens(final int size, final LittleEndianInput in) {
        final List<Ptg> temp = new ArrayList<Ptg>(4 + size / 2);
        int pos = 0;
        boolean hasArrayPtgs = false;
        while (pos < size) {
            final Ptg ptg = createPtg(in);
            if (ptg instanceof ArrayPtg.Initial) {
                hasArrayPtgs = true;
            }
            pos += ptg.getSize();
            temp.add(ptg);
        }
        if (pos != size) {
            throw new RuntimeException("Ptg array size mismatch");
        }
        if (hasArrayPtgs) {
            final Ptg[] result = toPtgArray(temp);
            for (int i = 0; i < result.length; ++i) {
                if (result[i] instanceof ArrayPtg.Initial) {
                    result[i] = ((ArrayPtg.Initial)result[i]).finishReading(in);
                }
            }
            return result;
        }
        return toPtgArray(temp);
    }
    
    public static Ptg createPtg(final LittleEndianInput in) {
        final byte id = in.readByte();
        if (id < 32) {
            return createBasePtg(id, in);
        }
        final Ptg retval = createClassifiedPtg(id, in);
        if (id >= 96) {
            retval.setClass((byte)64);
        }
        else if (id >= 64) {
            retval.setClass((byte)32);
        }
        else {
            retval.setClass((byte)0);
        }
        return retval;
    }
    
    private static Ptg createClassifiedPtg(final byte id, final LittleEndianInput in) {
        final int baseId = (id & 0x1F) | 0x20;
        switch (baseId) {
            case 32: {
                return new ArrayPtg.Initial(in);
            }
            case 33: {
                return FuncPtg.create(in);
            }
            case 34: {
                return FuncVarPtg.create(in);
            }
            case 35: {
                return new NamePtg(in);
            }
            case 36: {
                return new RefPtg(in);
            }
            case 37: {
                return new AreaPtg(in);
            }
            case 38: {
                return new MemAreaPtg(in);
            }
            case 39: {
                return new MemErrPtg(in);
            }
            case 41: {
                return new MemFuncPtg(in);
            }
            case 42: {
                return new RefErrorPtg(in);
            }
            case 43: {
                return new AreaErrPtg(in);
            }
            case 44: {
                return new RefNPtg(in);
            }
            case 45: {
                return new AreaNPtg(in);
            }
            case 57: {
                return new NameXPtg(in);
            }
            case 58: {
                return new Ref3DPtg(in);
            }
            case 59: {
                return new Area3DPtg(in);
            }
            case 60: {
                return new DeletedRef3DPtg(in);
            }
            case 61: {
                return new DeletedArea3DPtg(in);
            }
            default: {
                throw new UnsupportedOperationException(" Unknown Ptg in Formula: 0x" + Integer.toHexString(id) + " (" + id + ")");
            }
        }
    }
    
    private static Ptg createBasePtg(final byte id, final LittleEndianInput in) {
        switch (id) {
            case 0: {
                return new UnknownPtg(id);
            }
            case 1: {
                return new ExpPtg(in);
            }
            case 2: {
                return new TblPtg(in);
            }
            case 3: {
                return AddPtg.instance;
            }
            case 4: {
                return SubtractPtg.instance;
            }
            case 5: {
                return MultiplyPtg.instance;
            }
            case 6: {
                return DividePtg.instance;
            }
            case 7: {
                return PowerPtg.instance;
            }
            case 8: {
                return ConcatPtg.instance;
            }
            case 9: {
                return LessThanPtg.instance;
            }
            case 10: {
                return LessEqualPtg.instance;
            }
            case 11: {
                return EqualPtg.instance;
            }
            case 12: {
                return GreaterEqualPtg.instance;
            }
            case 13: {
                return GreaterThanPtg.instance;
            }
            case 14: {
                return NotEqualPtg.instance;
            }
            case 15: {
                return IntersectionPtg.instance;
            }
            case 16: {
                return UnionPtg.instance;
            }
            case 17: {
                return RangePtg.instance;
            }
            case 18: {
                return UnaryPlusPtg.instance;
            }
            case 19: {
                return UnaryMinusPtg.instance;
            }
            case 20: {
                return PercentPtg.instance;
            }
            case 21: {
                return ParenthesisPtg.instance;
            }
            case 22: {
                return MissingArgPtg.instance;
            }
            case 23: {
                return new StringPtg(in);
            }
            case 25: {
                return new AttrPtg(in);
            }
            case 28: {
                return ErrPtg.read(in);
            }
            case 29: {
                return BoolPtg.read(in);
            }
            case 30: {
                return new IntPtg(in);
            }
            case 31: {
                return new NumberPtg(in);
            }
            default: {
                throw new RuntimeException("Unexpected base token id (" + id + ")");
            }
        }
    }
    
    private static Ptg[] toPtgArray(final List<Ptg> l) {
        if (l.isEmpty()) {
            return Ptg.EMPTY_PTG_ARRAY;
        }
        final Ptg[] result = new Ptg[l.size()];
        l.toArray(result);
        return result;
    }
    
    public static int getEncodedSize(final Ptg[] ptgs) {
        int result = 0;
        for (int i = 0; i < ptgs.length; ++i) {
            result += ptgs[i].getSize();
        }
        return result;
    }
    
    public static int getEncodedSizeWithoutArrayData(final Ptg[] ptgs) {
        int result = 0;
        for (int i = 0; i < ptgs.length; ++i) {
            final Ptg ptg = ptgs[i];
            if (ptg instanceof ArrayPtg) {
                result += 8;
            }
            else {
                result += ptg.getSize();
            }
        }
        return result;
    }
    
    public static int serializePtgs(final Ptg[] ptgs, final byte[] array, final int offset) {
        final int nTokens = ptgs.length;
        final LittleEndianByteArrayOutputStream out = new LittleEndianByteArrayOutputStream(array, offset);
        List<Ptg> arrayPtgs = null;
        for (final Ptg ptg : ptgs) {
            ptg.write(out);
            if (ptg instanceof ArrayPtg) {
                if (arrayPtgs == null) {
                    arrayPtgs = new ArrayList<Ptg>(5);
                }
                arrayPtgs.add(ptg);
            }
        }
        if (arrayPtgs != null) {
            for (int i = 0; i < arrayPtgs.size(); ++i) {
                final ArrayPtg p = arrayPtgs.get(i);
                p.writeTokenValueBytes(out);
            }
        }
        return out.getWriteIndex() - offset;
    }
    
    public abstract int getSize();
    
    public abstract void write(final LittleEndianOutput p0);
    
    public abstract String toFormulaString();
    
    @Override
    public String toString() {
        return this.getClass().toString();
    }
    
    public final void setClass(final byte thePtgClass) {
        if (this.isBaseToken()) {
            throw new RuntimeException("setClass should not be called on a base token");
        }
        this.ptgClass = thePtgClass;
    }
    
    public final byte getPtgClass() {
        return this.ptgClass;
    }
    
    public final char getRVAType() {
        if (this.isBaseToken()) {
            return '.';
        }
        switch (this.ptgClass) {
            case 0: {
                return 'R';
            }
            case 32: {
                return 'V';
            }
            case 64: {
                return 'A';
            }
            default: {
                throw new RuntimeException("Unknown operand class (" + this.ptgClass + ")");
            }
        }
    }
    
    public abstract byte getDefaultOperandClass();
    
    public abstract boolean isBaseToken();
    
    public static boolean doesFormulaReferToDeletedCell(final Ptg[] ptgs) {
        for (int i = 0; i < ptgs.length; ++i) {
            if (isDeletedCellRef(ptgs[i])) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean isDeletedCellRef(final Ptg ptg) {
        return ptg == ErrPtg.REF_INVALID || ptg instanceof DeletedArea3DPtg || ptg instanceof DeletedRef3DPtg || ptg instanceof AreaErrPtg || ptg instanceof RefErrorPtg;
    }
    
    static {
        EMPTY_PTG_ARRAY = new Ptg[0];
    }
}
