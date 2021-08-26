// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.util;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class LazilyConcatenatedByteArray
{
    private final List<byte[]> arrays;
    
    public LazilyConcatenatedByteArray() {
        this.arrays = new ArrayList<byte[]>(1);
    }
    
    public void clear() {
        this.arrays.clear();
    }
    
    public void concatenate(final byte[] array) {
        if (array == null) {
            throw new IllegalArgumentException("array cannot be null");
        }
        this.arrays.add(array);
    }
    
    public byte[] toArray() {
        if (this.arrays.isEmpty()) {
            return null;
        }
        if (this.arrays.size() > 1) {
            int totalLength = 0;
            for (final byte[] array : this.arrays) {
                totalLength += array.length;
            }
            final byte[] concatenated = new byte[totalLength];
            int destPos = 0;
            for (final byte[] array2 : this.arrays) {
                System.arraycopy(array2, 0, concatenated, destPos, array2.length);
                destPos += array2.length;
            }
            this.arrays.clear();
            this.arrays.add(concatenated);
        }
        return this.arrays.get(0);
    }
}
