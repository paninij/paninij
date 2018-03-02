package org.paninij.proc.check.capsule.procedures;

import org.paninij.lang.Capsule;
import org.paninij.lang.Future;
import org.paninij.lang.String;
import org.paninij.lang.Block;

@Capsule
class BlockAndFutureCore {
    @Block @Future
    int methodA() {
        return 0;
    }
}