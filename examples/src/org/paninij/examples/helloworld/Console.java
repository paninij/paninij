package org.paninij.examples.helloworld;

import org.paninij.lang.Capsule;

@Capsule
class Console implements Stream
{
    public void design() { ; }

    public void write(String s) {
        System.out.println(s);
    }
}
