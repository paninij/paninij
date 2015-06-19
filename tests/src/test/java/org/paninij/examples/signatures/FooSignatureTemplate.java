package org.paninij.examples.signatures;

import org.paninij.lang.Future;
import org.paninij.lang.Signature;

@Signature
public interface FooSignatureTemplate
{
    @Future
    public void setGreeting(String greeting);
}
