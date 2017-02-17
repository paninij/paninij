package org.paninij.proc.check.capsule.events;

import org.paninij.lang.Capsule;
import org.paninij.lang.Handler;
import org.paninij.lang.Block;

@Capsule
class HandlerWithBlockCore
{
    @Handler @Block void handler(String msg) {
        // Nothing to do here.        
    }
}
