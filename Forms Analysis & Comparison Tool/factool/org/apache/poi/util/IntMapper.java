// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.util;

import java.util.Iterator;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class IntMapper<T>
{
    private List<T> elements;
    private Map<T, Integer> valueKeyMap;
    private static final int _default_size = 10;
    
    public IntMapper() {
        this(10);
    }
    
    public IntMapper(final int initialCapacity) {
        this.elements = new ArrayList<T>(initialCapacity);
        this.valueKeyMap = new HashMap<T, Integer>(initialCapacity);
    }
    
    public boolean add(final T value) {
        final int index = this.elements.size();
        this.elements.add(value);
        this.valueKeyMap.put(value, index);
        return true;
    }
    
    public int size() {
        return this.elements.size();
    }
    
    public T get(final int index) {
        return this.elements.get(index);
    }
    
    public int getIndex(final T o) {
        final Integer i = this.valueKeyMap.get(o);
        if (i == null) {
            return -1;
        }
        return i;
    }
    
    public Iterator<T> iterator() {
        return this.elements.iterator();
    }
}
