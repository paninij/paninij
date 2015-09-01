package org.paninij.proc.shapes.voided;

import org.paninij.lang.Block;
import org.paninij.lang.Capsule;
import org.paninij.lang.Future;


@Capsule
public class VoidTemplate {
    public void foo(Object o) {

    }

    public void primitiveArg(int i) {

    }

    public void arrayArg(Object[] i) {

    }

    public void primitiveArrayArg(short[] b) {

    }

    @Block
    public void blockedVoid() {

    }

    @Future
    public void futureVoid() {

    }
}