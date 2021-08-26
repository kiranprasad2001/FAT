// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.util;

import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.Collections;
import java.util.Map;
import org.apache.poi.ss.usermodel.Color;

public class HSSFColor implements Color
{
    private static Map<Integer, HSSFColor> indexHash;
    
    public static final Map<Integer, HSSFColor> getIndexHash() {
        if (HSSFColor.indexHash == null) {
            HSSFColor.indexHash = Collections.unmodifiableMap((Map<? extends Integer, ? extends HSSFColor>)createColorsByIndexMap());
        }
        return HSSFColor.indexHash;
    }
    
    public static final Hashtable<Integer, HSSFColor> getMutableIndexHash() {
        return createColorsByIndexMap();
    }
    
    private static Hashtable<Integer, HSSFColor> createColorsByIndexMap() {
        final HSSFColor[] colors = getAllColors();
        final Hashtable<Integer, HSSFColor> result = new Hashtable<Integer, HSSFColor>(colors.length * 3 / 2);
        for (int i = 0; i < colors.length; ++i) {
            final HSSFColor color = colors[i];
            final Integer index1 = (Integer)color.getIndex();
            if (result.containsKey(index1)) {
                final HSSFColor prevColor = result.get(index1);
                throw new RuntimeException("Dup color index (" + index1 + ") for colors (" + prevColor.getClass().getName() + "),(" + color.getClass().getName() + ")");
            }
            result.put(index1, color);
        }
        for (int i = 0; i < colors.length; ++i) {
            final HSSFColor color = colors[i];
            final Integer index2 = getIndex2(color);
            if (index2 != null) {
                if (result.containsKey(index2)) {}
                result.put(index2, color);
            }
        }
        return result;
    }
    
    private static Integer getIndex2(final HSSFColor color) {
        Field f;
        try {
            f = color.getClass().getDeclaredField("index2");
        }
        catch (NoSuchFieldException e3) {
            return null;
        }
        Short s;
        try {
            s = (Short)f.get(color);
        }
        catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e2) {
            throw new RuntimeException(e2);
        }
        return (int)s;
    }
    
    private static HSSFColor[] getAllColors() {
        return new HSSFColor[] { new BLACK(), new BROWN(), new OLIVE_GREEN(), new DARK_GREEN(), new DARK_TEAL(), new DARK_BLUE(), new INDIGO(), new GREY_80_PERCENT(), new ORANGE(), new DARK_YELLOW(), new GREEN(), new TEAL(), new BLUE(), new BLUE_GREY(), new GREY_50_PERCENT(), new RED(), new LIGHT_ORANGE(), new LIME(), new SEA_GREEN(), new AQUA(), new LIGHT_BLUE(), new VIOLET(), new GREY_40_PERCENT(), new PINK(), new GOLD(), new YELLOW(), new BRIGHT_GREEN(), new TURQUOISE(), new DARK_RED(), new SKY_BLUE(), new PLUM(), new GREY_25_PERCENT(), new ROSE(), new LIGHT_YELLOW(), new LIGHT_GREEN(), new LIGHT_TURQUOISE(), new PALE_BLUE(), new LAVENDER(), new WHITE(), new CORNFLOWER_BLUE(), new LEMON_CHIFFON(), new MAROON(), new ORCHID(), new CORAL(), new ROYAL_BLUE(), new LIGHT_CORNFLOWER_BLUE(), new TAN() };
    }
    
    public static final Hashtable<String, HSSFColor> getTripletHash() {
        return createColorsByHexStringMap();
    }
    
    private static Hashtable<String, HSSFColor> createColorsByHexStringMap() {
        final HSSFColor[] colors = getAllColors();
        final Hashtable<String, HSSFColor> result = new Hashtable<String, HSSFColor>(colors.length * 3 / 2);
        for (int i = 0; i < colors.length; ++i) {
            final HSSFColor color = colors[i];
            final String hexString = color.getHexString();
            if (result.containsKey(hexString)) {
                final HSSFColor other = result.get(hexString);
                throw new RuntimeException("Dup color hexString (" + hexString + ") for color (" + color.getClass().getName() + ") - " + " already taken by (" + other.getClass().getName() + ")");
            }
            result.put(hexString, color);
        }
        return result;
    }
    
    public short getIndex() {
        return 8;
    }
    
    public short[] getTriplet() {
        return BLACK.triplet;
    }
    
    public String getHexString() {
        return "0:0:0";
    }
    
    public static final class BLACK extends HSSFColor
    {
        public static final short index = 8;
        public static final short[] triplet;
        public static final String hexString = "0:0:0";
        
        @Override
        public short getIndex() {
            return 8;
        }
        
        @Override
        public short[] getTriplet() {
            return BLACK.triplet;
        }
        
        @Override
        public String getHexString() {
            return "0:0:0";
        }
        
        static {
            triplet = new short[] { 0, 0, 0 };
        }
    }
    
    public static final class BROWN extends HSSFColor
    {
        public static final short index = 60;
        public static final short[] triplet;
        public static final String hexString = "9999:3333:0";
        
        @Override
        public short getIndex() {
            return 60;
        }
        
        @Override
        public short[] getTriplet() {
            return BROWN.triplet;
        }
        
        @Override
        public String getHexString() {
            return "9999:3333:0";
        }
        
        static {
            triplet = new short[] { 153, 51, 0 };
        }
    }
    
    public static class OLIVE_GREEN extends HSSFColor
    {
        public static final short index = 59;
        public static final short[] triplet;
        public static final String hexString = "3333:3333:0";
        
        @Override
        public short getIndex() {
            return 59;
        }
        
        @Override
        public short[] getTriplet() {
            return OLIVE_GREEN.triplet;
        }
        
        @Override
        public String getHexString() {
            return "3333:3333:0";
        }
        
        static {
            triplet = new short[] { 51, 51, 0 };
        }
    }
    
    public static final class DARK_GREEN extends HSSFColor
    {
        public static final short index = 58;
        public static final short[] triplet;
        public static final String hexString = "0:3333:0";
        
        @Override
        public short getIndex() {
            return 58;
        }
        
        @Override
        public short[] getTriplet() {
            return DARK_GREEN.triplet;
        }
        
        @Override
        public String getHexString() {
            return "0:3333:0";
        }
        
        static {
            triplet = new short[] { 0, 51, 0 };
        }
    }
    
    public static final class DARK_TEAL extends HSSFColor
    {
        public static final short index = 56;
        public static final short[] triplet;
        public static final String hexString = "0:3333:6666";
        
        @Override
        public short getIndex() {
            return 56;
        }
        
        @Override
        public short[] getTriplet() {
            return DARK_TEAL.triplet;
        }
        
        @Override
        public String getHexString() {
            return "0:3333:6666";
        }
        
        static {
            triplet = new short[] { 0, 51, 102 };
        }
    }
    
    public static final class DARK_BLUE extends HSSFColor
    {
        public static final short index = 18;
        public static final short index2 = 32;
        public static final short[] triplet;
        public static final String hexString = "0:0:8080";
        
        @Override
        public short getIndex() {
            return 18;
        }
        
        @Override
        public short[] getTriplet() {
            return DARK_BLUE.triplet;
        }
        
        @Override
        public String getHexString() {
            return "0:0:8080";
        }
        
        static {
            triplet = new short[] { 0, 0, 128 };
        }
    }
    
    public static final class INDIGO extends HSSFColor
    {
        public static final short index = 62;
        public static final short[] triplet;
        public static final String hexString = "3333:3333:9999";
        
        @Override
        public short getIndex() {
            return 62;
        }
        
        @Override
        public short[] getTriplet() {
            return INDIGO.triplet;
        }
        
        @Override
        public String getHexString() {
            return "3333:3333:9999";
        }
        
        static {
            triplet = new short[] { 51, 51, 153 };
        }
    }
    
    public static final class GREY_80_PERCENT extends HSSFColor
    {
        public static final short index = 63;
        public static final short[] triplet;
        public static final String hexString = "3333:3333:3333";
        
        @Override
        public short getIndex() {
            return 63;
        }
        
        @Override
        public short[] getTriplet() {
            return GREY_80_PERCENT.triplet;
        }
        
        @Override
        public String getHexString() {
            return "3333:3333:3333";
        }
        
        static {
            triplet = new short[] { 51, 51, 51 };
        }
    }
    
    public static final class DARK_RED extends HSSFColor
    {
        public static final short index = 16;
        public static final short index2 = 37;
        public static final short[] triplet;
        public static final String hexString = "8080:0:0";
        
        @Override
        public short getIndex() {
            return 16;
        }
        
        @Override
        public short[] getTriplet() {
            return DARK_RED.triplet;
        }
        
        @Override
        public String getHexString() {
            return "8080:0:0";
        }
        
        static {
            triplet = new short[] { 128, 0, 0 };
        }
    }
    
    public static final class ORANGE extends HSSFColor
    {
        public static final short index = 53;
        public static final short[] triplet;
        public static final String hexString = "FFFF:6666:0";
        
        @Override
        public short getIndex() {
            return 53;
        }
        
        @Override
        public short[] getTriplet() {
            return ORANGE.triplet;
        }
        
        @Override
        public String getHexString() {
            return "FFFF:6666:0";
        }
        
        static {
            triplet = new short[] { 255, 102, 0 };
        }
    }
    
    public static final class DARK_YELLOW extends HSSFColor
    {
        public static final short index = 19;
        public static final short[] triplet;
        public static final String hexString = "8080:8080:0";
        
        @Override
        public short getIndex() {
            return 19;
        }
        
        @Override
        public short[] getTriplet() {
            return DARK_YELLOW.triplet;
        }
        
        @Override
        public String getHexString() {
            return "8080:8080:0";
        }
        
        static {
            triplet = new short[] { 128, 128, 0 };
        }
    }
    
    public static final class GREEN extends HSSFColor
    {
        public static final short index = 17;
        public static final short[] triplet;
        public static final String hexString = "0:8080:0";
        
        @Override
        public short getIndex() {
            return 17;
        }
        
        @Override
        public short[] getTriplet() {
            return GREEN.triplet;
        }
        
        @Override
        public String getHexString() {
            return "0:8080:0";
        }
        
        static {
            triplet = new short[] { 0, 128, 0 };
        }
    }
    
    public static final class TEAL extends HSSFColor
    {
        public static final short index = 21;
        public static final short index2 = 38;
        public static final short[] triplet;
        public static final String hexString = "0:8080:8080";
        
        @Override
        public short getIndex() {
            return 21;
        }
        
        @Override
        public short[] getTriplet() {
            return TEAL.triplet;
        }
        
        @Override
        public String getHexString() {
            return "0:8080:8080";
        }
        
        static {
            triplet = new short[] { 0, 128, 128 };
        }
    }
    
    public static final class BLUE extends HSSFColor
    {
        public static final short index = 12;
        public static final short index2 = 39;
        public static final short[] triplet;
        public static final String hexString = "0:0:FFFF";
        
        @Override
        public short getIndex() {
            return 12;
        }
        
        @Override
        public short[] getTriplet() {
            return BLUE.triplet;
        }
        
        @Override
        public String getHexString() {
            return "0:0:FFFF";
        }
        
        static {
            triplet = new short[] { 0, 0, 255 };
        }
    }
    
    public static final class BLUE_GREY extends HSSFColor
    {
        public static final short index = 54;
        public static final short[] triplet;
        public static final String hexString = "6666:6666:9999";
        
        @Override
        public short getIndex() {
            return 54;
        }
        
        @Override
        public short[] getTriplet() {
            return BLUE_GREY.triplet;
        }
        
        @Override
        public String getHexString() {
            return "6666:6666:9999";
        }
        
        static {
            triplet = new short[] { 102, 102, 153 };
        }
    }
    
    public static final class GREY_50_PERCENT extends HSSFColor
    {
        public static final short index = 23;
        public static final short[] triplet;
        public static final String hexString = "8080:8080:8080";
        
        @Override
        public short getIndex() {
            return 23;
        }
        
        @Override
        public short[] getTriplet() {
            return GREY_50_PERCENT.triplet;
        }
        
        @Override
        public String getHexString() {
            return "8080:8080:8080";
        }
        
        static {
            triplet = new short[] { 128, 128, 128 };
        }
    }
    
    public static final class RED extends HSSFColor
    {
        public static final short index = 10;
        public static final short[] triplet;
        public static final String hexString = "FFFF:0:0";
        
        @Override
        public short getIndex() {
            return 10;
        }
        
        @Override
        public short[] getTriplet() {
            return RED.triplet;
        }
        
        @Override
        public String getHexString() {
            return "FFFF:0:0";
        }
        
        static {
            triplet = new short[] { 255, 0, 0 };
        }
    }
    
    public static final class LIGHT_ORANGE extends HSSFColor
    {
        public static final short index = 52;
        public static final short[] triplet;
        public static final String hexString = "FFFF:9999:0";
        
        @Override
        public short getIndex() {
            return 52;
        }
        
        @Override
        public short[] getTriplet() {
            return LIGHT_ORANGE.triplet;
        }
        
        @Override
        public String getHexString() {
            return "FFFF:9999:0";
        }
        
        static {
            triplet = new short[] { 255, 153, 0 };
        }
    }
    
    public static final class LIME extends HSSFColor
    {
        public static final short index = 50;
        public static final short[] triplet;
        public static final String hexString = "9999:CCCC:0";
        
        @Override
        public short getIndex() {
            return 50;
        }
        
        @Override
        public short[] getTriplet() {
            return LIME.triplet;
        }
        
        @Override
        public String getHexString() {
            return "9999:CCCC:0";
        }
        
        static {
            triplet = new short[] { 153, 204, 0 };
        }
    }
    
    public static final class SEA_GREEN extends HSSFColor
    {
        public static final short index = 57;
        public static final short[] triplet;
        public static final String hexString = "3333:9999:6666";
        
        @Override
        public short getIndex() {
            return 57;
        }
        
        @Override
        public short[] getTriplet() {
            return SEA_GREEN.triplet;
        }
        
        @Override
        public String getHexString() {
            return "3333:9999:6666";
        }
        
        static {
            triplet = new short[] { 51, 153, 102 };
        }
    }
    
    public static final class AQUA extends HSSFColor
    {
        public static final short index = 49;
        public static final short[] triplet;
        public static final String hexString = "3333:CCCC:CCCC";
        
        @Override
        public short getIndex() {
            return 49;
        }
        
        @Override
        public short[] getTriplet() {
            return AQUA.triplet;
        }
        
        @Override
        public String getHexString() {
            return "3333:CCCC:CCCC";
        }
        
        static {
            triplet = new short[] { 51, 204, 204 };
        }
    }
    
    public static final class LIGHT_BLUE extends HSSFColor
    {
        public static final short index = 48;
        public static final short[] triplet;
        public static final String hexString = "3333:6666:FFFF";
        
        @Override
        public short getIndex() {
            return 48;
        }
        
        @Override
        public short[] getTriplet() {
            return LIGHT_BLUE.triplet;
        }
        
        @Override
        public String getHexString() {
            return "3333:6666:FFFF";
        }
        
        static {
            triplet = new short[] { 51, 102, 255 };
        }
    }
    
    public static final class VIOLET extends HSSFColor
    {
        public static final short index = 20;
        public static final short index2 = 36;
        public static final short[] triplet;
        public static final String hexString = "8080:0:8080";
        
        @Override
        public short getIndex() {
            return 20;
        }
        
        @Override
        public short[] getTriplet() {
            return VIOLET.triplet;
        }
        
        @Override
        public String getHexString() {
            return "8080:0:8080";
        }
        
        static {
            triplet = new short[] { 128, 0, 128 };
        }
    }
    
    public static final class GREY_40_PERCENT extends HSSFColor
    {
        public static final short index = 55;
        public static final short[] triplet;
        public static final String hexString = "9696:9696:9696";
        
        @Override
        public short getIndex() {
            return 55;
        }
        
        @Override
        public short[] getTriplet() {
            return GREY_40_PERCENT.triplet;
        }
        
        @Override
        public String getHexString() {
            return "9696:9696:9696";
        }
        
        static {
            triplet = new short[] { 150, 150, 150 };
        }
    }
    
    public static final class PINK extends HSSFColor
    {
        public static final short index = 14;
        public static final short index2 = 33;
        public static final short[] triplet;
        public static final String hexString = "FFFF:0:FFFF";
        
        @Override
        public short getIndex() {
            return 14;
        }
        
        @Override
        public short[] getTriplet() {
            return PINK.triplet;
        }
        
        @Override
        public String getHexString() {
            return "FFFF:0:FFFF";
        }
        
        static {
            triplet = new short[] { 255, 0, 255 };
        }
    }
    
    public static final class GOLD extends HSSFColor
    {
        public static final short index = 51;
        public static final short[] triplet;
        public static final String hexString = "FFFF:CCCC:0";
        
        @Override
        public short getIndex() {
            return 51;
        }
        
        @Override
        public short[] getTriplet() {
            return GOLD.triplet;
        }
        
        @Override
        public String getHexString() {
            return "FFFF:CCCC:0";
        }
        
        static {
            triplet = new short[] { 255, 204, 0 };
        }
    }
    
    public static final class YELLOW extends HSSFColor
    {
        public static final short index = 13;
        public static final short index2 = 34;
        public static final short[] triplet;
        public static final String hexString = "FFFF:FFFF:0";
        
        @Override
        public short getIndex() {
            return 13;
        }
        
        @Override
        public short[] getTriplet() {
            return YELLOW.triplet;
        }
        
        @Override
        public String getHexString() {
            return "FFFF:FFFF:0";
        }
        
        static {
            triplet = new short[] { 255, 255, 0 };
        }
    }
    
    public static final class BRIGHT_GREEN extends HSSFColor
    {
        public static final short index = 11;
        public static final short index2 = 35;
        public static final short[] triplet;
        public static final String hexString = "0:FFFF:0";
        
        @Override
        public short getIndex() {
            return 11;
        }
        
        @Override
        public String getHexString() {
            return "0:FFFF:0";
        }
        
        @Override
        public short[] getTriplet() {
            return BRIGHT_GREEN.triplet;
        }
        
        static {
            triplet = new short[] { 0, 255, 0 };
        }
    }
    
    public static final class TURQUOISE extends HSSFColor
    {
        public static final short index = 15;
        public static final short index2 = 35;
        public static final short[] triplet;
        public static final String hexString = "0:FFFF:FFFF";
        
        @Override
        public short getIndex() {
            return 15;
        }
        
        @Override
        public short[] getTriplet() {
            return TURQUOISE.triplet;
        }
        
        @Override
        public String getHexString() {
            return "0:FFFF:FFFF";
        }
        
        static {
            triplet = new short[] { 0, 255, 255 };
        }
    }
    
    public static final class SKY_BLUE extends HSSFColor
    {
        public static final short index = 40;
        public static final short[] triplet;
        public static final String hexString = "0:CCCC:FFFF";
        
        @Override
        public short getIndex() {
            return 40;
        }
        
        @Override
        public short[] getTriplet() {
            return SKY_BLUE.triplet;
        }
        
        @Override
        public String getHexString() {
            return "0:CCCC:FFFF";
        }
        
        static {
            triplet = new short[] { 0, 204, 255 };
        }
    }
    
    public static final class PLUM extends HSSFColor
    {
        public static final short index = 61;
        public static final short index2 = 25;
        public static final short[] triplet;
        public static final String hexString = "9999:3333:6666";
        
        @Override
        public short getIndex() {
            return 61;
        }
        
        @Override
        public short[] getTriplet() {
            return PLUM.triplet;
        }
        
        @Override
        public String getHexString() {
            return "9999:3333:6666";
        }
        
        static {
            triplet = new short[] { 153, 51, 102 };
        }
    }
    
    public static final class GREY_25_PERCENT extends HSSFColor
    {
        public static final short index = 22;
        public static final short[] triplet;
        public static final String hexString = "C0C0:C0C0:C0C0";
        
        @Override
        public short getIndex() {
            return 22;
        }
        
        @Override
        public short[] getTriplet() {
            return GREY_25_PERCENT.triplet;
        }
        
        @Override
        public String getHexString() {
            return "C0C0:C0C0:C0C0";
        }
        
        static {
            triplet = new short[] { 192, 192, 192 };
        }
    }
    
    public static final class ROSE extends HSSFColor
    {
        public static final short index = 45;
        public static final short[] triplet;
        public static final String hexString = "FFFF:9999:CCCC";
        
        @Override
        public short getIndex() {
            return 45;
        }
        
        @Override
        public short[] getTriplet() {
            return ROSE.triplet;
        }
        
        @Override
        public String getHexString() {
            return "FFFF:9999:CCCC";
        }
        
        static {
            triplet = new short[] { 255, 153, 204 };
        }
    }
    
    public static final class TAN extends HSSFColor
    {
        public static final short index = 47;
        public static final short[] triplet;
        public static final String hexString = "FFFF:CCCC:9999";
        
        @Override
        public short getIndex() {
            return 47;
        }
        
        @Override
        public short[] getTriplet() {
            return TAN.triplet;
        }
        
        @Override
        public String getHexString() {
            return "FFFF:CCCC:9999";
        }
        
        static {
            triplet = new short[] { 255, 204, 153 };
        }
    }
    
    public static final class LIGHT_YELLOW extends HSSFColor
    {
        public static final short index = 43;
        public static final short[] triplet;
        public static final String hexString = "FFFF:FFFF:9999";
        
        @Override
        public short getIndex() {
            return 43;
        }
        
        @Override
        public short[] getTriplet() {
            return LIGHT_YELLOW.triplet;
        }
        
        @Override
        public String getHexString() {
            return "FFFF:FFFF:9999";
        }
        
        static {
            triplet = new short[] { 255, 255, 153 };
        }
    }
    
    public static final class LIGHT_GREEN extends HSSFColor
    {
        public static final short index = 42;
        public static final short[] triplet;
        public static final String hexString = "CCCC:FFFF:CCCC";
        
        @Override
        public short getIndex() {
            return 42;
        }
        
        @Override
        public short[] getTriplet() {
            return LIGHT_GREEN.triplet;
        }
        
        @Override
        public String getHexString() {
            return "CCCC:FFFF:CCCC";
        }
        
        static {
            triplet = new short[] { 204, 255, 204 };
        }
    }
    
    public static final class LIGHT_TURQUOISE extends HSSFColor
    {
        public static final short index = 41;
        public static final short index2 = 27;
        public static final short[] triplet;
        public static final String hexString = "CCCC:FFFF:FFFF";
        
        @Override
        public short getIndex() {
            return 41;
        }
        
        @Override
        public short[] getTriplet() {
            return LIGHT_TURQUOISE.triplet;
        }
        
        @Override
        public String getHexString() {
            return "CCCC:FFFF:FFFF";
        }
        
        static {
            triplet = new short[] { 204, 255, 255 };
        }
    }
    
    public static final class PALE_BLUE extends HSSFColor
    {
        public static final short index = 44;
        public static final short[] triplet;
        public static final String hexString = "9999:CCCC:FFFF";
        
        @Override
        public short getIndex() {
            return 44;
        }
        
        @Override
        public short[] getTriplet() {
            return PALE_BLUE.triplet;
        }
        
        @Override
        public String getHexString() {
            return "9999:CCCC:FFFF";
        }
        
        static {
            triplet = new short[] { 153, 204, 255 };
        }
    }
    
    public static final class LAVENDER extends HSSFColor
    {
        public static final short index = 46;
        public static final short[] triplet;
        public static final String hexString = "CCCC:9999:FFFF";
        
        @Override
        public short getIndex() {
            return 46;
        }
        
        @Override
        public short[] getTriplet() {
            return LAVENDER.triplet;
        }
        
        @Override
        public String getHexString() {
            return "CCCC:9999:FFFF";
        }
        
        static {
            triplet = new short[] { 204, 153, 255 };
        }
    }
    
    public static final class WHITE extends HSSFColor
    {
        public static final short index = 9;
        public static final short[] triplet;
        public static final String hexString = "FFFF:FFFF:FFFF";
        
        @Override
        public short getIndex() {
            return 9;
        }
        
        @Override
        public short[] getTriplet() {
            return WHITE.triplet;
        }
        
        @Override
        public String getHexString() {
            return "FFFF:FFFF:FFFF";
        }
        
        static {
            triplet = new short[] { 255, 255, 255 };
        }
    }
    
    public static final class CORNFLOWER_BLUE extends HSSFColor
    {
        public static final short index = 24;
        public static final short[] triplet;
        public static final String hexString = "9999:9999:FFFF";
        
        @Override
        public short getIndex() {
            return 24;
        }
        
        @Override
        public short[] getTriplet() {
            return CORNFLOWER_BLUE.triplet;
        }
        
        @Override
        public String getHexString() {
            return "9999:9999:FFFF";
        }
        
        static {
            triplet = new short[] { 153, 153, 255 };
        }
    }
    
    public static final class LEMON_CHIFFON extends HSSFColor
    {
        public static final short index = 26;
        public static final short[] triplet;
        public static final String hexString = "FFFF:FFFF:CCCC";
        
        @Override
        public short getIndex() {
            return 26;
        }
        
        @Override
        public short[] getTriplet() {
            return LEMON_CHIFFON.triplet;
        }
        
        @Override
        public String getHexString() {
            return "FFFF:FFFF:CCCC";
        }
        
        static {
            triplet = new short[] { 255, 255, 204 };
        }
    }
    
    public static final class MAROON extends HSSFColor
    {
        public static final short index = 25;
        public static final short[] triplet;
        public static final String hexString = "8000:0:0";
        
        @Override
        public short getIndex() {
            return 25;
        }
        
        @Override
        public short[] getTriplet() {
            return MAROON.triplet;
        }
        
        @Override
        public String getHexString() {
            return "8000:0:0";
        }
        
        static {
            triplet = new short[] { 127, 0, 0 };
        }
    }
    
    public static final class ORCHID extends HSSFColor
    {
        public static final short index = 28;
        public static final short[] triplet;
        public static final String hexString = "6666:0:6666";
        
        @Override
        public short getIndex() {
            return 28;
        }
        
        @Override
        public short[] getTriplet() {
            return ORCHID.triplet;
        }
        
        @Override
        public String getHexString() {
            return "6666:0:6666";
        }
        
        static {
            triplet = new short[] { 102, 0, 102 };
        }
    }
    
    public static final class CORAL extends HSSFColor
    {
        public static final short index = 29;
        public static final short[] triplet;
        public static final String hexString = "FFFF:8080:8080";
        
        @Override
        public short getIndex() {
            return 29;
        }
        
        @Override
        public short[] getTriplet() {
            return CORAL.triplet;
        }
        
        @Override
        public String getHexString() {
            return "FFFF:8080:8080";
        }
        
        static {
            triplet = new short[] { 255, 128, 128 };
        }
    }
    
    public static final class ROYAL_BLUE extends HSSFColor
    {
        public static final short index = 30;
        public static final short[] triplet;
        public static final String hexString = "0:6666:CCCC";
        
        @Override
        public short getIndex() {
            return 30;
        }
        
        @Override
        public short[] getTriplet() {
            return ROYAL_BLUE.triplet;
        }
        
        @Override
        public String getHexString() {
            return "0:6666:CCCC";
        }
        
        static {
            triplet = new short[] { 0, 102, 204 };
        }
    }
    
    public static final class LIGHT_CORNFLOWER_BLUE extends HSSFColor
    {
        public static final short index = 31;
        public static final short[] triplet;
        public static final String hexString = "CCCC:CCCC:FFFF";
        
        @Override
        public short getIndex() {
            return 31;
        }
        
        @Override
        public short[] getTriplet() {
            return LIGHT_CORNFLOWER_BLUE.triplet;
        }
        
        @Override
        public String getHexString() {
            return "CCCC:CCCC:FFFF";
        }
        
        static {
            triplet = new short[] { 204, 204, 255 };
        }
    }
    
    public static final class AUTOMATIC extends HSSFColor
    {
        private static HSSFColor instance;
        public static final short index = 64;
        
        @Override
        public short getIndex() {
            return 64;
        }
        
        @Override
        public short[] getTriplet() {
            return BLACK.triplet;
        }
        
        @Override
        public String getHexString() {
            return "0:0:0";
        }
        
        public static HSSFColor getInstance() {
            return AUTOMATIC.instance;
        }
        
        static {
            AUTOMATIC.instance = new AUTOMATIC();
        }
    }
}
