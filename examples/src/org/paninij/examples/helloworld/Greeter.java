package org.paninij.examples.helloworld;

import org.paninij.lang.Panini;

@Panini
class Greeter
{
    String message;
    Stream s;

    public void init() {
        message = "Hello World!";
    }

    public void design(Stream s) {
        this.s = s;
    }

    public void wire() { ; }

    public void greet() {
        s.write("Panini: " + message);
        long time = System.currentTimeMillis();
        s.write("Time is now: " + time);
    }
}
