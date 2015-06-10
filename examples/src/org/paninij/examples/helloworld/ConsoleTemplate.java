package org.paninij.examples.helloworld;

import org.paninij.lang.Capsule;
import org.paninij.lang.String;

@Capsule
class ConsoleTemplate implements Stream
{
    @Override
    public void write(String s) {
        System.out.println(s);
    }
}
