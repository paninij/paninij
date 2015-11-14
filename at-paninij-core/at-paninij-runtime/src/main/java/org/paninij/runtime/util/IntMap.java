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
import java.util.HashSet;
import java.util.Set;


// TODO: Everything! (This is an extrememly naive implementation.)
public class IntMap<T>
{
    private static final int DEFAULT_INIT_CAPACITY = 8;

    private T[] data;
    private int capacity;
    

    @SuppressWarnings("unchecked")
    public IntMap()
    {
        data = (T[]) new Object[DEFAULT_INIT_CAPACITY];
        capacity = DEFAULT_INIT_CAPACITY;
    }
    

    public void put(int key, T value)
    {
        if (value == null) {
            throw new IllegalArgumentException("The given value cannot be `null`.");
        }
        while (key >= capacity) {
            growArray();
        }
        data[key] = value;
    }
    
    
    /**
     * @return The value mapped to the given `key` or `null` if no such value has been associated
     *         with the given key.
     */
    public T get(int key)
    {
        return (key >= capacity) ? null : data[key];
    }
    

    public boolean contains(T obj)
    {
        for (int idx = 0; idx < data.length; idx++) {
            if (obj == data[idx])
                return true;
        }
        return false;
    }


    public void clear()
    {
        // TODO: Shrink capacity if appropriate.
        Arrays.fill(data, null);
    }
    
    
    // TODO: Make this use something more like an `int` set.
    public Set<Integer> keySet()
    {
        Set<Integer> keySet = new HashSet<Integer>();
        for (int idx = 0; idx < data.length; idx++)
        {
            if (data[idx] != null) {
                keySet.add(idx);
            }
        }
        return keySet;
    }    
    
    
    private void growArray()
    {
        capacity = capacity << 1;
        assert capacity >= DEFAULT_INIT_CAPACITY;

        @SuppressWarnings("unchecked")
        T[] new_data = (T[]) new Object[capacity];
        for (int idx = 0; idx < data.length; idx++) {
            new_data[idx] = data[idx];
        }
        data = new_data;
    }
}
