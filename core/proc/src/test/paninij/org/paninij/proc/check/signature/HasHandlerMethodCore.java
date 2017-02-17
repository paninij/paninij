package org.paninij.proc.check.signature;

import org.paninij.lang.Signature;
import org.paninij.lang.Handler;;

@Signature
interface HasHandlerMethodCore
{
    @Handler void proc(String msg) {
        // Nothing needed here.
    }
}
