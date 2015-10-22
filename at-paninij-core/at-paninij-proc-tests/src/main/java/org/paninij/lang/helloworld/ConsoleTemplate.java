package org.paninij.lang.helloworld;

import org.paninij.lang.Capsule;
import org.paninij.lang.String;

@Capsule
class ConsoleTemplate implements StreamTemplate
{
    public void write(String s) {
        System.out.println(s);
    }
}
