package org.paninij.ex.helloworld;

import org.paninij.lang.Signature;
import org.paninij.lang.String;

@Signature
public interface StreamTemplate {
    void write(String s);
}