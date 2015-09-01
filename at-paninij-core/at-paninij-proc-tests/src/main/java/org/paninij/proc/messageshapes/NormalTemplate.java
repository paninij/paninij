package org.paninij.proc.messageshapes;

import org.paninij.lang.Capsule;


@Capsule
public class NormalTemplate
{
    public Object foo(Object o, Object i) {
        return new Object();
    }

    public Object primitiveArg(int i)
    {
        return new Object();
    }

//    public Object tooManyArgs(boolean a, boolean b, boolean c, boolean d, boolean e, boolean f,
//                              boolean g, boolean h, boolean i, boolean j, boolean k, boolean l,
//                              boolean m, boolean n, boolean o, boolean p, boolean q, boolean r,
//                              boolean s, boolean t, boolean u, boolean v, boolean w, boolean x,
//                              boolean y, boolean z)
//    {
//        return new Object();
//    }

    public Object arrayArg(Object[] arr) {
        return new Object();
    }

    public Object primitiveArrayArg(int[] arr) {
        return new Object();
    }
}