package org.paninij.examples.helloworld;

import org.paninij.lang.Panini;

@Panini
class Console implements Stream
{
    public void design() { ; }

    public void write(String s) {
        System.out.println(s);
    }
}
