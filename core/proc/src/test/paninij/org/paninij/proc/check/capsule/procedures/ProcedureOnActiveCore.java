package org.paninij.proc.check.capsule.procedures;

import org.paninij.lang.Capsule;
import org.paninij.lang.Future;
import org.paninij.lang.String;
import org.paninij.lang.Duck;
import org.paninij.lang.Future;
import org.paninij.lang.Block;

@Capsule
class ProcedureOnActiveCore {
    void procA() {
        // Nothing needed here.
    }

    void run() {
        // Nothing needed here.
    }

    @Duck @Future
    int methodA() {
        return 0;
    }

    @Duck @Block
    int methodB() {
        return 0;
    }

    @Future @Block
    int methodC() {
        return 0;
    }
}
