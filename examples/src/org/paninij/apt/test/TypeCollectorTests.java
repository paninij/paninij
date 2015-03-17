package org.paninij.apt.test;

import org.paninij.apt.test.Visit;


@Visit
public class TypeCollectorTests
{
    Object method0() {
        return null;
    }
    
    Object[] method1() {
        return null;
    }
    
    String method2() {
        return null;
    }
    
    A method3(B b) {
        return null;
    }
    
    C<D> method4() {
        return null;
    }
    
    private static interface M { /* Nothing here */ }
    private static interface N { /* Nothing here */ }
    
    private static class A { /* Nothing here */ }
    private static class B { /* Nothing here */ }
    private static class C<T> { /* Nothing here */ }
    private static class D { /* Nothing here */ }
}
