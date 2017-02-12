package org.paninij.ex.helloworld;

import org.paninij.lang.Capsule;
import org.paninij.lang.String;

@Capsule
class ConsoleCore implements StreamCore
{
    public void write(String s) {
        System.out.println(s);
    }
}
