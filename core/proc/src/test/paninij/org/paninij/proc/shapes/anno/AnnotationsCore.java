package org.paninij.proc.shapes.anno;

import org.paninij.lang.Block;
import org.paninij.lang.Capsule;
import org.paninij.lang.Future;


@Capsule
class AnnotationsCore {

    void unannotatedVoid() {

    }

    @Block
    void atBlockVoid() {

    }

    @Future
    void atFutureVoid() {

    }

    @Block
    String atBlockFinal() {
        return "I'm blocked";
    }

    @Future
    String atFutureFinal() {
        return "I'm a future";
    }

    @Future
    String atFutureFinalArgs(int some, boolean other) {
        return "Hello World!";
    }

    @Future
    String atBlockFinalArgs(int some, boolean other) {
        return "Hello World!";
    }

    @Future
    boolean atFuturePrimitive() {
        return true;
    }

    @Block
    boolean atBlockPrimitive() {
        return true;
    }


    Object unannotatedObject() {
        return new Object();
    }

    @Block
    Object atBlockObject() {
        return new Object();
    }

    @Future
    Object atFutureObject() {
        return new Object();
    }
}
