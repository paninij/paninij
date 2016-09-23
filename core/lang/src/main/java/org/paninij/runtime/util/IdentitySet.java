/*******************************************************************************
 * This file is part of the Panini project at Iowa State University.
 *
 * @PaniniJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * @PaniniJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with @PaniniJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributors:
 * 	Dr. Hridesh Rajan,
 * 	Dalton Mills,
 * 	David Johnston,
 * 	Trey Erenberger
 *******************************************************************************/
package org.paninij.runtime.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

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
  

    public void addAll(IdentitySet<T> that)
    {
        for (int idx = 0; idx < that.size; idx++) {
            this.add(that.data[idx]);
        }
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


    public static <T> IdentitySet<T> make(Set<T> set)
    {
        IdentitySet<T> result = new IdentitySet<T>();
        for (T elem : set) {
            result.add(elem);
        }
        return result;
    }


    public boolean isDisjointFrom(IdentitySet<T> that)
    {
        for (int i = 0; i < this.size; i++)
        {
            for (int j = 0; j < that.size; j++)
            {
                if (this.data[i] == that.data[j]) {
                    return false;
                }
            }
        }

        return true;
    }

    public String toString()
    {
        if (size == 0)
            return "{}";

        StringBuilder builder = new StringBuilder();
        builder.append("{");
        for (int idx = 0; idx < size - 1; idx++) {
            builder.append(data[idx].toString() + ", ");
        }
        builder.append(data[size - 1].toString() + "}");
        return builder.toString();
    }


    public boolean isEmpty()
    {
        return size == 0;
    }
}
