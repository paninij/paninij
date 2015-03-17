package org.paninij.apt.test;

import org.paninij.apt.test.Visit;


@Visit
public class TypeCollectorTests
{
    /* Test that the return types are being collected */

    A method0() {
        return null;
    }
    
    B[] method1() {
        return null;
    }
    
    D<C> method2() {
        return null;
    }
    
    private static class A { /* Nothing here */ }
    private static class B { /* Nothing here */ }
    private static class C { /* Nothing here */ }
    private static class D<T> { /* Nothing here */ }

    
    /* Test that arguments are also begin collected */
    
    void method3(E e, F f) {
        /* Nothing here */
    }

    void method4(G[] g) {
        /* Nothing here */
    }

    void method5(I<H> h) {
        /* Nothing here */
    }
    
    private static class E { /* Nothing here */ }
    private static class F { /* Nothing here */ }
    private static class G { /* Nothing here */ }
    private static class H { /* Nothing here */ }
    private static class I<T> { /* Nothing here */ }
    
    
    /* Test that interfaces are also being collected */
    
    private static interface M { /* Nothing here */ }
    private static interface N { /* Nothing here */ }
    private static interface O { /* Nothing here */ }
    private static interface P<T> { /* Nothing here */ }
    
    void method6(M m, N n) {
        /* Nothing here */
    }
    
    void method7(P<O> p) {
        /* Nothing here */
    }
}
