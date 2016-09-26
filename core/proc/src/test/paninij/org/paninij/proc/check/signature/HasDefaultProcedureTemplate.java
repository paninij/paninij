package org.paninij.proc.check.signature;

import org.paninij.lang.Signature;

@Signature
public interface HasDefaultProcedureTemplate {
    default void proc() {
        // Nothing needed here.
    }
}
