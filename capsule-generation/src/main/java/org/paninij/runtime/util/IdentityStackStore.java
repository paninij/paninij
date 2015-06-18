package org.paninij.runtime.util;

public class IdentityStackStore extends ThreadLocal<IdentityStack>
{
    protected IdentityStack initialValue()
    {
        return new IdentityStack();
    }
}
