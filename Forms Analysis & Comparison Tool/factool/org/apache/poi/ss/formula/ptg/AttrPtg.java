// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.ptg;

import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.BitField;

public final class AttrPtg extends ControlPtg
{
    public static final byte sid = 25;
    private static final int SIZE = 4;
    private final byte _options;
    private final short _data;
    private final int[] _jumpTable;
    private final int _chooseFuncOffset;
    private static final BitField semiVolatile;
    private static final BitField optiIf;
    private static final BitField optiChoose;
    private static final BitField optiSkip;
    private static final BitField optiSum;
    private static final BitField baxcel;
    private static final BitField space;
    public static final AttrPtg SUM;
    
    public AttrPtg(final LittleEndianInput in) {
        this._options = in.readByte();
        this._data = in.readShort();
        if (this.isOptimizedChoose()) {
            final int nCases = this._data;
            final int[] jumpTable = new int[nCases];
            for (int i = 0; i < jumpTable.length; ++i) {
                jumpTable[i] = in.readUShort();
            }
            this._jumpTable = jumpTable;
            this._chooseFuncOffset = in.readUShort();
        }
        else {
            this._jumpTable = null;
            this._chooseFuncOffset = -1;
        }
    }
    
    private AttrPtg(final int options, final int data, final int[] jt, final int chooseFuncOffset) {
        this._options = (byte)options;
        this._data = (short)data;
        this._jumpTable = jt;
        this._chooseFuncOffset = chooseFuncOffset;
    }
    
    public static AttrPtg createSpace(final int type, final int count) {
        final int data = (type & 0xFF) | (count << 8 & 0xFFFF);
        return new AttrPtg(AttrPtg.space.set(0), data, null, -1);
    }
    
    public static AttrPtg createIf(final int dist) {
        return new AttrPtg(AttrPtg.optiIf.set(0), dist, null, -1);
    }
    
    public static AttrPtg createSkip(final int dist) {
        return new AttrPtg(AttrPtg.optiSkip.set(0), dist, null, -1);
    }
    
    public static AttrPtg getSumSingle() {
        return new AttrPtg(AttrPtg.optiSum.set(0), 0, null, -1);
    }
    
    public boolean isSemiVolatile() {
        return AttrPtg.semiVolatile.isSet(this._options);
    }
    
    public boolean isOptimizedIf() {
        return AttrPtg.optiIf.isSet(this._options);
    }
    
    public boolean isOptimizedChoose() {
        return AttrPtg.optiChoose.isSet(this._options);
    }
    
    public boolean isSum() {
        return AttrPtg.optiSum.isSet(this._options);
    }
    
    public boolean isSkip() {
        return AttrPtg.optiSkip.isSet(this._options);
    }
    
    private boolean isBaxcel() {
        return AttrPtg.baxcel.isSet(this._options);
    }
    
    public boolean isSpace() {
        return AttrPtg.space.isSet(this._options);
    }
    
    public short getData() {
        return this._data;
    }
    
    public int[] getJumpTable() {
        return this._jumpTable.clone();
    }
    
    public int getChooseFuncOffset() {
        if (this._jumpTable == null) {
            throw new IllegalStateException("Not tAttrChoose");
        }
        return this._chooseFuncOffset;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(64);
        sb.append(this.getClass().getName()).append(" [");
        if (this.isSemiVolatile()) {
            sb.append("volatile ");
        }
        if (this.isSpace()) {
            sb.append("space count=").append(this._data >> 8 & 0xFF);
            sb.append(" type=").append(this._data & 0xFF).append(" ");
        }
        if (this.isOptimizedIf()) {
            sb.append("if dist=").append(this._data);
        }
        else if (this.isOptimizedChoose()) {
            sb.append("choose nCases=").append(this._data);
        }
        else if (this.isSkip()) {
            sb.append("skip dist=").append(this._data);
        }
        else if (this.isSum()) {
            sb.append("sum ");
        }
        else if (this.isBaxcel()) {
            sb.append("assign ");
        }
        sb.append("]");
        return sb.toString();
    }
    
    @Override
    public void write(final LittleEndianOutput out) {
        out.writeByte(25 + this.getPtgClass());
        out.writeByte(this._options);
        out.writeShort(this._data);
        final int[] jt = this._jumpTable;
        if (jt != null) {
            for (int i = 0; i < jt.length; ++i) {
                out.writeShort(jt[i]);
            }
            out.writeShort(this._chooseFuncOffset);
        }
    }
    
    @Override
    public int getSize() {
        if (this._jumpTable != null) {
            return 4 + (this._jumpTable.length + 1) * 2;
        }
        return 4;
    }
    
    public String toFormulaString(final String[] operands) {
        if (AttrPtg.space.isSet(this._options)) {
            return operands[0];
        }
        if (AttrPtg.optiIf.isSet(this._options)) {
            return this.toFormulaString() + "(" + operands[0] + ")";
        }
        if (AttrPtg.optiSkip.isSet(this._options)) {
            return this.toFormulaString() + operands[0];
        }
        return this.toFormulaString() + "(" + operands[0] + ")";
    }
    
    public int getNumberOfOperands() {
        return 1;
    }
    
    public int getType() {
        return -1;
    }
    
    @Override
    public String toFormulaString() {
        if (AttrPtg.semiVolatile.isSet(this._options)) {
            return "ATTR(semiVolatile)";
        }
        if (AttrPtg.optiIf.isSet(this._options)) {
            return "IF";
        }
        if (AttrPtg.optiChoose.isSet(this._options)) {
            return "CHOOSE";
        }
        if (AttrPtg.optiSkip.isSet(this._options)) {
            return "";
        }
        if (AttrPtg.optiSum.isSet(this._options)) {
            return "SUM";
        }
        if (AttrPtg.baxcel.isSet(this._options)) {
            return "ATTR(baxcel)";
        }
        if (AttrPtg.space.isSet(this._options)) {
            return "";
        }
        return "UNKNOWN ATTRIBUTE";
    }
    
    static {
        semiVolatile = BitFieldFactory.getInstance(1);
        optiIf = BitFieldFactory.getInstance(2);
        optiChoose = BitFieldFactory.getInstance(4);
        optiSkip = BitFieldFactory.getInstance(8);
        optiSum = BitFieldFactory.getInstance(16);
        baxcel = BitFieldFactory.getInstance(32);
        space = BitFieldFactory.getInstance(64);
        SUM = new AttrPtg(16, 0, null, -1);
    }
    
    public static final class SpaceType
    {
        public static final int SPACE_BEFORE = 0;
        public static final int CR_BEFORE = 1;
        public static final int SPACE_BEFORE_OPEN_PAREN = 2;
        public static final int CR_BEFORE_OPEN_PAREN = 3;
        public static final int SPACE_BEFORE_CLOSE_PAREN = 4;
        public static final int CR_BEFORE_CLOSE_PAREN = 5;
        public static final int SPACE_AFTER_EQUALITY = 6;
        
        private SpaceType() {
        }
    }
}
