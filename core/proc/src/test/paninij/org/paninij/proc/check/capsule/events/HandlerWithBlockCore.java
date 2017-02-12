package org.paninij.proc.check.capsule.events;

import org.paninij.lang.Capsule;
import org.paninij.lang.Handler;
import org.paninij.lang.Block;

@Capsule
public class HandlerWithBlockTemplate
{
    @Handler @Block public void handler(String msg) {
        // Nothing to do here.        
    }
}
