package org.paninij.examples;

import org.paninij.lang.Capsule;

@Capsule
public class Greeter implements GreeterSignature
{
    private String greeting;

    @Override
    public String greet()
    {
        return greeting;
    }

    @Override
    public void setGreeting(String greeting)
    {
        this.greeting = greeting;
    }

}
