package org.paninij.runtime;

public interface ResolvableFuture<T> extends Future<T>
{
    void panini$resolve(T t);
    //boolean panini$isResolved = false;
}
