package org.paninij.runtime.util;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Implements a monotonically-increasing set (i.e. identities cannot be removed except by clearing
 * the whole set).
 */
public class IdentitySet<T> implements Iterable<T>
{
    private static final int DEFAULT_INIT_CAPACITY = 8;

    private T[] data;
    private int size;
    private int capacity;
    

    @SuppressWarnings("unchecked")
    public IdentitySet()
    {
        data = (T[]) new Object[DEFAULT_INIT_CAPACITY];
        size = 0;
        capacity = DEFAULT_INIT_CAPACITY;
    }
    

    /**
     * @return True iff the added object is new to the set.
     */
    public boolean add(T obj)
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
    

    public boolean contains(T obj)
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
    public Iterator<T> iterator()
    {
        return new Iter();
    }


    private void growArray()
    {
        capacity = capacity << 1;
        assert capacity >= DEFAULT_INIT_CAPACITY;

        @SuppressWarnings("unchecked")
        T[] new_data = (T[]) new Object[capacity];
        for (int idx = 0; idx < size; idx++) {
            new_data[idx] = data[idx];
        }
        data = new_data;
    }


    private class Iter implements Iterator<T>
    {
        private int cur;
        
        @Override
        public boolean hasNext() {
            return cur < size;
        }

        @Override
        public T next() {
            return data[cur++];
        }
    }
}
