package org.paninij.proc.signatures;

import org.paninij.lang.Capsule;
import org.paninij.lang.Future;

@Capsule
public class FooTemplate implements FooSignatureTemplate
{
    String greeting;

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
