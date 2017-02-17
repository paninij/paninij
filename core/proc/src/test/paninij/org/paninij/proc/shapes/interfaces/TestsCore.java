package org.paninij.proc.shapes.interfaces;

import org.paninij.lang.Block;
import org.paninij.lang.Capsule;
import org.paninij.lang.Future;

@Capsule
class TestsCore
{
    MyInterface getThing() {
        return new MyImplementation();
    }

    @Block
    MyInterface blockGetThing() {
        return new MyImplementation();
    }

    @Future
    MyInterface futureGetThing() {
        return new MyImplementation();
    }
}
