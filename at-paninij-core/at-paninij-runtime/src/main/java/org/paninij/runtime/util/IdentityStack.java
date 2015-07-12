package org.paninij.runtime.util;

import java.util.Stack;

@SuppressWarnings("serial")
public class IdentityStack<T> extends Stack<T>
{
    /**
     * Pushes the given `obj` onto the top of the stack unless `obj` is already in the stack. If
     * `obj` was already in the stack, then `null is returned. Otherwise, `obj` itself is returned.
     */
    public T push(T obj)
    {
        return (super.contains(obj)) ? null : super.push(obj);
    }
    
    public T pop()
    {
        return isEmpty() ? null : super.pop();
    }
}
