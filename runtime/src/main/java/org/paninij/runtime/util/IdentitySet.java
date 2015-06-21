package org.paninij.runtime.util;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Implements a monotonically-increasing set (i.e. identities cannot be removed except by clearing
 * the whole set).
 */
public class IdentitySet implements Iterable<Object>
{
    private static final int DEFAULT_INIT_CAPACITY = 8;

    private Object[] data;
    private int size;
    private int capacity;
    

    public IdentitySet()
    {
        data = new Object[DEFAULT_INIT_CAPACITY];
        size = 0;
        capacity = DEFAULT_INIT_CAPACITY;
    }
    

    public boolean add(Object obj)
    {
        if (contains(obj)) {
            return false;
        }

        if (size == capacity) {
            growArray();
        }
        data[size] = obj;
        size++;
        return true;
    }
    

    public boolean contains(Object obj)
    {
        for (int idx = 0; idx < size; idx++) {
            if (obj == data[idx])
                return true;
        }
        return false;
    }


    public void clear()
    {
        // TODO: Shrink capacity if appropriate.
        size = 0;
        Arrays.fill(data, null);
    }
    
    
    @Override
    public Iterator<Object> iterator()
    {
        return new Iter();
    }


    private void growArray()
    {
        capacity = capacity << 1;
        assert capacity >= DEFAULT_INIT_CAPACITY;

        Object[] new_data = new Object[capacity];
        for (int idx = 0; idx < size; idx++) {
            new_data[idx] = data[idx];
        }
        data = new_data;
    }


    private class Iter implements Iterator<Object>
    {
        private int cur;
        
        @Override
        public boolean hasNext() {
            return cur < size;
        }

        @Override
        public Object next() {
            return data[cur++];
        }
    }
}
