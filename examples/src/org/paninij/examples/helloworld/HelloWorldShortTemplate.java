package org.paninij.examples.helloworld;

import org.paninij.lang.Capsule;
import org.paninij.lang.String;


@Capsule
public class HelloWorldShortTemplate
{
    void run() {
        System.out.println("@PaniniJ: Hello World!");
    }

    public String getMessage() {
        return new String("vinegar");
    }
}
