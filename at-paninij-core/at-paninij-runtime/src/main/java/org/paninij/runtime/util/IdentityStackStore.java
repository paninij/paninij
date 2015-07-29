package org.paninij.runtime.util;

public class IdentityStackStore<T> extends ThreadLocal<IdentityStack<T>>
{
    protected IdentityStack<T> initialValue()
    {
        return new IdentityStack<T>();
    }
}
