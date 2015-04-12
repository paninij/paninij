package org.paninij.examples.helloworld;

import org.paninij.lang.Capsule;
import org.paninij.lang.String;

@Capsule
class ConsoleTemplate implements Stream
{
    void design(Console self) { ; }

    public void write(String s) {
        System.out.println(s);
    }
}
