package org.paninij.examples.shapes.interfaces;

import org.paninij.lang.Block;
import org.paninij.lang.Capsule;
import org.paninij.lang.Future;

@Capsule
public class TestsTemplate
{
    public MyInterface getThing() {
        return new MyImplementation();
    }

    @Block
    public MyInterface blockGetThing() {
        return new MyImplementation();
    }

    @Future
    public MyInterface futureGetThing() {
        return new MyImplementation();
    }
}
