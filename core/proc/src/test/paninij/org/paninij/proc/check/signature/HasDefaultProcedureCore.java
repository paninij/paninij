package org.paninij.proc.check.signature;

import org.paninij.lang.Signature;

@Signature
public interface HasDefaultProcedureCore {
    default void proc() {
        // Nothing needed here.
    }
}
