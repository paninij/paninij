package org.paninij.examples;

import org.paninij.lang.Capsule;


@Capsule
public class MethodShapes
{
    private void zeroArgs() {
        return;
    }

    public void oneArg(int x) {
        return;
    }

    public void twoArgs(int x, int y) {
        return;
    }

    public int retInt() {
        return 0;
    }

    public Integer retInteger() {
        return new Integer(0);
    }
}
