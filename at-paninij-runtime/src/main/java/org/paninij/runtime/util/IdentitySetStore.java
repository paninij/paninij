package org.paninij.runtime.util;

public class IdentitySetStore extends ThreadLocal<IdentitySet>
{
    protected IdentitySet initialValue()
    {
        return new IdentitySet();
    }
}
