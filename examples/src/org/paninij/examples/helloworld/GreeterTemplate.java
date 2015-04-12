package org.paninij.examples.helloworld;

import org.paninij.lang.Capsule;
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

    void design(Greeter self, Stream s) { ; }

    public void greet() {
        s.write(new String("Panini: " + message));
        long time = System.currentTimeMillis();
        s.write(new String("Time is now: " + time));
    }
}
