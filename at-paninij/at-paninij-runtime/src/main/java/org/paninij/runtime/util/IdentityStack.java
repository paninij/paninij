package org.paninij.runtime.util;

import java.util.Stack;

@SuppressWarnings("serial")
public class IdentityStack extends Stack<Object>
{
    /**
     * Pushes the given `obj` onto the top of the stack unless `obj` is already in the stack. If
     * `obj` was already in the stack, then `null is returned. Otherwise, `obj` itself is returned.
     */
    public Object push(Object obj)
    {
        return (super.contains(obj)) ? null : super.push(obj);
    }
    
    public Object pop()
    {
        return isEmpty() ? null : super.pop();
    }
}
