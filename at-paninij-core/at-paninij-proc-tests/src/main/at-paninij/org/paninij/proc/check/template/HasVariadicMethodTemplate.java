package org.paninij.proc.check.template;

import org.paninij.lang.Capsule;

@BadTemplate
@Capsule
public class HasVariadicMethodTemplate
{
    public Object foo(Object o, Object i) {
        return new Object();
    }

    public Object primitiveArg(int i)
    {
        return new Object();
    }

    public Object arrayArg(Object[] arr) {
        return new Object();
    }

    public Object primitiveArrayArg(int[] arr) {
        return new Object();
    } 

    public Object variadicArg(int i, int... integers) {
        return new Object();
    } 
}
