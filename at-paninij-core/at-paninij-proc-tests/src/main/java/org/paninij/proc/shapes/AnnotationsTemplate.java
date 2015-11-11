package org.paninij.proc.shapes;

import org.paninij.lang.Block;
import org.paninij.lang.Capsule;
import org.paninij.lang.Future;


@Capsule
public class AnnotationsTemplate {

    public void unannotatedVoid() {

    }

    @Block
    public void atBlockVoid() {

    }

    @Future
    public void atFutureVoid() {

    }

    @Block
    public String atBlockFinal() {
        return "I'm blocked";
    }

    @Future
    public String atFutureFinal() {
        return "I'm a future";
    }

    @Future
    public String atFutureFinalArgs(int some, boolean other) {
        return "Hello World!";
    }

    @Future
    public String atBlockFinalArgs(int some, boolean other) {
        return "Hello World!";
    }

    @Future
    public boolean atFuturePrimitive() {
        return true;
    }

    @Block
    public boolean atBlockPrimitive() {
        return true;
    }


    public Object unannotatedObject() {
        return new Object();
    }

    @Block
    public Object atBlockObject() {
        return new Object();
    }

    @Future
    public Object atFutureObject() {
        return new Object();
    }
}