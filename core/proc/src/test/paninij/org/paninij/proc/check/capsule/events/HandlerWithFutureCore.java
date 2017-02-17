package org.paninij.proc.check.capsule.events;

import org.paninij.lang.Capsule;
import org.paninij.lang.Handler;
import org.paninij.lang.Future;

@Capsule
class HandlerWithFutureCore
{
    @Handler @Future void handler(String msg) {
        // Nothing to do here.        
    }
}
