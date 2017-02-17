package org.paninij.proc.check.capsule.events;

import org.paninij.lang.Capsule;
import org.paninij.lang.Handler;
import org.paninij.lang.Duck;

@Capsule
class HandlerWithDuckCore
{
    @Handler @Duck void handler(String msg) {
        // Nothing to do here.        
    }
}
