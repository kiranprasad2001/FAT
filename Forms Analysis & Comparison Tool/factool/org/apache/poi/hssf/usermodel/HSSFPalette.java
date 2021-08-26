// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.record.PaletteRecord;

public final class HSSFPalette
{
    private PaletteRecord _palette;
    
    protected HSSFPalette(final PaletteRecord palette) {
        this._palette = palette;
    }
    
    public HSSFColor getColor(final short index) {
        if (index == 64) {
            return HSSFColor.AUTOMATIC.getInstance();
        }
        final byte[] b = this._palette.getColor(index);
        if (b != null) {
            return new CustomColor(index, b);
        }
        return null;
    }
    
    public HSSFColor getColor(final int index) {
        return this.getColor((short)index);
    }
    
    public HSSFColor findColor(final byte red, final byte green, final byte blue) {
        byte[] b = this._palette.getColor(8);
        PaletteRecord palette;
        for (short i = 8; b != null; b = palette.getColor(i)) {
            if (b[0] == red && b[1] == green && b[2] == blue) {
                return new CustomColor(i, b);
            }
            palette = this._palette;
            ++i;
        }
        return null;
    }
    
    public HSSFColor findSimilarColor(final byte red, final byte green, final byte blue) {
        return this.findSimilarColor(this.unsignedInt(red), this.unsignedInt(green), this.unsignedInt(blue));
    }
    
    public HSSFColor findSimilarColor(final int red, final int green, final int blue) {
        HSSFColor result = null;
        int minColorDistance = Integer.MAX_VALUE;
        byte[] b = this._palette.getColor(8);
        PaletteRecord palette;
        for (short i = 8; b != null; b = palette.getColor(i)) {
            final int colorDistance = Math.abs(red - this.unsignedInt(b[0])) + Math.abs(green - this.unsignedInt(b[1])) + Math.abs(blue - this.unsignedInt(b[2]));
            if (colorDistance < minColorDistance) {
                minColorDistance = colorDistance;
                result = this.getColor(i);
            }
            palette = this._palette;
            ++i;
        }
        return result;
    }
    
    private int unsignedInt(final byte b) {
        return 0xFF & b;
    }
    
    public void setColorAtIndex(final short index, final byte red, final byte green, final byte blue) {
        this._palette.setColor(index, red, green, blue);
    }
    
    public HSSFColor addColor(final byte red, final byte green, final byte blue) {
        byte[] b = this._palette.getColor(8);
        PaletteRecord palette;
        for (short i = 8; i < 64; ++i, b = palette.getColor(i)) {
            if (b == null) {
                this.setColorAtIndex(i, red, green, blue);
                return this.getColor(i);
            }
            palette = this._palette;
        }
        throw new RuntimeException("Could not find free color index");
    }
    
    private static final class CustomColor extends HSSFColor
    {
        private short _byteOffset;
        private byte _red;
        private byte _green;
        private byte _blue;
        
        public CustomColor(final short byteOffset, final byte[] colors) {
            this(byteOffset, colors[0], colors[1], colors[2]);
        }
        
        private CustomColor(final short byteOffset, final byte red, final byte green, final byte blue) {
            this._byteOffset = byteOffset;
            this._red = red;
            this._green = green;
            this._blue = blue;
        }
        
        @Override
        public short getIndex() {
            return this._byteOffset;
        }
        
        @Override
        public short[] getTriplet() {
            return new short[] { (short)(this._red & 0xFF), (short)(this._green & 0xFF), (short)(this._blue & 0xFF) };
        }
        
        @Override
        public String getHexString() {
            final StringBuffer sb = new StringBuffer();
            sb.append(this.getGnumericPart(this._red));
            sb.append(':');
            sb.append(this.getGnumericPart(this._green));
            sb.append(':');
            sb.append(this.getGnumericPart(this._blue));
            return sb.toString();
        }
        
        private String getGnumericPart(final byte color) {
            String s;
            if (color == 0) {
                s = "0";
            }
            else {
                int c = color & 0xFF;
                c |= c << 8;
                for (s = Integer.toHexString(c).toUpperCase(); s.length() < 4; s = "0" + s) {}
            }
            return s;
        }
    }
}
