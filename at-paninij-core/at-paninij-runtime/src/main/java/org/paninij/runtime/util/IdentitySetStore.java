package org.paninij.runtime.util;

public class IdentitySetStore<T> extends ThreadLocal<IdentitySet<T>>
{
    protected IdentitySet<T> initialValue()
    {
        return new IdentitySet<T>();
    }
}
