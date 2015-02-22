package org.paninij.examples.helloworld;

import org.paninij.lang.Capsule;

@Capsule
class Greeter
{
    String message;
    StreamSignature s;

    public void init() {
        message = "Hello World!";
    }

    public void design(StreamSignature s) {
        this.s = s;
    }

    public void wire() { ; }

    public void greet() {
        s.write("Panini: " + message);
        long time = System.currentTimeMillis();
        s.write("Time is now: " + time);
    }
}
