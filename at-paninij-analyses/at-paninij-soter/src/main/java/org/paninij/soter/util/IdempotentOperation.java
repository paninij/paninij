package org.paninij.soter.util;

public interface IdempotentOperation
{
    /**
     * If this method on this object instance has never been called before, then the operation is
     * performed. If this method has been previously called, then it will return immediately without
     * doing anything.
     */
    void perform();
}
