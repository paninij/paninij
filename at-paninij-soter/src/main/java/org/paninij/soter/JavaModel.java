package org.paninij.soter;

import com.ibm.wala.classLoader.IMethod;

public class JavaModel
{
    public static boolean isNamed(IMethod method, String name)
    {
        return method.getName().toString().equals(name);
    }
}
