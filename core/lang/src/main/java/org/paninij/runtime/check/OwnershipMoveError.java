package org.paninij.runtime.check;

/**
 * Indicates that an ownership move (a.k.a. ownership transfer) was illegal and if it had
 * not been detected, could have violated capsule state encapsulation.
 */
public class OwnershipMoveError extends Error
{
    public OwnershipMoveError() {
        super();
    }

    public OwnershipMoveError(String msg) {
        super(msg);
    }
}
