package org.paninij.proc.codegen;

import org.paninij.lang.Capsule;

@Capsule
class HasVarargsMethodCore
{
    Object foo(Object o, Object i) {
        return new Object();
    }

    Object primitiveArg(int i)
    {
        return new Object();
    }

    Object arrayArg(Object[] arr) {
        return new Object();
    }

    Object primitiveArrayArg(int[] arr) {
        return new Object();
    } 

    Object variadicArg(int i, int... integers) {
        return new Object();
    } 
}
