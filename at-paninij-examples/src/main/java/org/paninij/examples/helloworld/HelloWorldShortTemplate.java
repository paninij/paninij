package org.paninij.examples.helloworld;

import org.paninij.lang.Capsule;
import org.paninij.lang.CapsuleSystem;
import org.paninij.lang.Root;

@Root
@Capsule
public class HelloWorldShortTemplate
{
    void run() {
        System.out.println("@PaniniJ: Hello World!");
    }
    
    public static void main(String[] args) {
        CapsuleSystem.start(HelloWorldShort.class, args);
    }
}
