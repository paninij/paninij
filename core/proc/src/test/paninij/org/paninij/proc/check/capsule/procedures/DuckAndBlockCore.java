package org.paninij.proc.check.capsule.procedures;

import org.paninij.lang.Capsule;
import org.paninij.lang.Block;
import org.paninij.lang.String;
import org.paninij.lang.Duck;

@Capsule
class DuckAndBlockCore {
    @Duck @Block
    int methodA() {
        return 0;
    }
}