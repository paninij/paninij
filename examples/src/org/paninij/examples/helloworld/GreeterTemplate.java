package org.paninij.examples.helloworld;

import org.paninij.lang.Capsule;
import org.paninij.lang.Future;
import org.paninij.lang.Wired;
import org.paninij.lang.String;

@Capsule
class GreeterTemplate
{
    String message;
    @Wired Stream s;

    void init() {
        message = new String("Hello World!");
    }

    @Future
    public void greet() {
        s.write(new String("Panini: " + message));
        long time = System.currentTimeMillis();
        s.write(new String("Time is now: " + time));
    }
}
