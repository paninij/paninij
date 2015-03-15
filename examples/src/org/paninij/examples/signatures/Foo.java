package org.paninij.examples.signatures;

import org.paninij.lang.Capsule;

@Capsule
public class Foo implements FooSignature
{
    private String greeting;

    @Override
    public void setGreeting(String greeting)
    {
        this.greeting = greeting;
    }
    
    public Object getObject()
    {
        return new Object();
    }

}
