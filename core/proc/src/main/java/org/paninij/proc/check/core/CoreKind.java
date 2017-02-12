package org.paninij.proc.check.core;

/**
 * @author dwtj
 */
public enum CoreKind
{
    CAPSULE,
    SIGNATURE;

    public String toString() {
        switch (this) {
            case CAPSULE: return "capsule";
            case SIGNATURE: return "signature";
            default: throw new IllegalStateException("Unknown `CoreKind`: " + this);
        }
    }
}
