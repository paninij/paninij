package org.paninij.examples.helloworld;

import org.paninij.lang.Capsule;

@Capsule
class Console implements StreamSignature
{
    public void init() { ; }
    public void design() { ; }
    public void wire() { ; }

    public void write(String s) {
        System.out.println(s);
    }
}
