package org.paninij.examples;

import org.paninij.lang.Signature;

@Signature
public interface GreeterSignature {
    String greet();
    void setGreeting(String greeting);
}
