// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.util;

import java.util.HashMap;
import java.util.Map;

public class BitFieldFactory
{
    private static Map<Integer, BitField> instances;
    
    public static BitField getInstance(final int mask) {
        BitField f = BitFieldFactory.instances.get(mask);
        if (f == null) {
            f = new BitField(mask);
            BitFieldFactory.instances.put(mask, f);
        }
        return f;
    }
    
    static {
        BitFieldFactory.instances = new HashMap<Integer, BitField>();
    }
}
