package org.paninij.proc.check.signature;

import org.paninij.lang.Signature;

@Signature
interface HasDefaultProcedureCore {
    default void proc() {
        // Nothing needed here.
    }
}
