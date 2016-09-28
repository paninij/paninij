package org.paninij.proc.check.template;

/**
 * @author dwtj
 */
public enum TemplateKind
{
    CAPSULE,
    SIGNATURE;

    public String toString() {
        switch (this) {
            case CAPSULE: return "capsule";
            case SIGNATURE: return "signature";
            default: throw new IllegalStateException("Unknown `TemplateKind`: " + this);
        }
    }
}
