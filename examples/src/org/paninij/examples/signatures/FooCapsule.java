package org.paninij.examples.signatures;

import org.paninij.lang.Capsule;

@Capsule
public class FooCapsule implements FooSignature
{
    private String greeting;

    /*
    @Override
    public String greet()
    {
        return greeting;
    }
    */

    @Override
    public void setGreeting(String greeting)
    {
        this.greeting = greeting;
    }

}
