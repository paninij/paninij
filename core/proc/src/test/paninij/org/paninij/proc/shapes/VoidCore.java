package org.paninij.proc.shapes;

import org.paninij.lang.Block;
import org.paninij.lang.Capsule;
import org.paninij.lang.Future;


@Capsule
class VoidCore
{
    void foo(Object o) {

    }

    void primitiveArg(int i) {

    }

    void arrayArg(Object[] i) {

    }

    void primitiveArrayArg(short[] b) {

    }

    @Block
    void blockedVoid() {

    }

    @Future
    void futureVoid() {

    }
}
