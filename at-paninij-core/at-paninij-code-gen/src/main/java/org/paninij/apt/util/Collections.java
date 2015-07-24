package org.paninij.apt.util;

import java.util.ArrayList;
import java.util.List;

public class Collections
{
    public static <T> List<T> makeSingletonList(T elem)
    {
        List<T> list = new ArrayList<T>();
        list.add(elem);
        return list;
    }

}
