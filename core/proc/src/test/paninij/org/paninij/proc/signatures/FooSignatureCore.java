package org.paninij.proc.signatures;

import org.paninij.lang.Future;
import org.paninij.lang.Signature;

@Signature
interface FooSignatureCore
{
    @Future
    void setGreeting(String greeting);
}
