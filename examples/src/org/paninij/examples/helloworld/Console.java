package org.paninij.examples.helloworld;

import org.paninij.lang.Panini;

@Panini
class Console implements Stream
{
    public void init() { ; }
    public void design() { ; }
    public void wire() { ; }

    public void write(String s) {
        System.out.println(s);
    }
}
