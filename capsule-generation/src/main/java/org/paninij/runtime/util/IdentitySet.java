package org.paninij.runtime.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class IdentitySet implements Set<Object>
{
    ArrayList<Object> data = new ArrayList<Object>();
    
    /**
     * @return Removes and returns an arbitrary object from set, or `null` if no such object exists.
     */
    public Object remove()
    {
        int size = data.size();
        return (size == 0) ? null : data.remove(size - 1);
    }
    
    @Override
    public int size()
    {
        return data.size();
    }

    @Override
    public boolean isEmpty()
    {
        return data.isEmpty();
    }

    @Override
    public boolean contains(Object o)
    {
        return data.contains(o);
    }

    @Override
    public Iterator<Object> iterator()
    {
        return data.iterator();
    }

    @Override
    public Object[] toArray()
    {
        return data.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a)
    {
        return data.toArray(a);
    }

    @Override
    public boolean add(Object e)
    {
        if (data.contains(e))
        {
            return false;
        }
        else
        {
            data.add(e);
            return true;
        }
    }

    @Override
    public boolean remove(Object o)
    {
        return data.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c)
    {
        return data.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Object> c)
    {
        return c.stream().anyMatch(obj -> add(obj));
    }

    @Override
    public boolean retainAll(Collection<?> c)
    {
        return data.retainAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c)
    {
        return data.removeAll(c);
    }

    @Override
    public void clear()
    {
        data.clear();
    }
}
