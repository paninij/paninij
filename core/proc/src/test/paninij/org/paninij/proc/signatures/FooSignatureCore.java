package org.paninij.proc.signatures;

import org.paninij.lang.Future;
import org.paninij.lang.Signature;

@Signature
public interface FooSignatureCore
{
    @Future
    public void setGreeting(String greeting);
}
