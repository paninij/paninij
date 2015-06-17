package org.paninij.examples.signatures;

import org.paninij.lang.Capsule;
import org.paninij.lang.Future;

@Capsule
public class FooTemplate implements FooSignatureTemplate
{
    private String greeting;

    @Override
    @Future
    public void setGreeting(String greeting)
    {
        this.greeting = greeting;
    }

    public Object getObject()
    {
        return new Object();
    }

}
