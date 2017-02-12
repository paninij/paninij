package org.paninij.proc.check.capsule;

import org.paninij.lang.Capsule;

@Capsule
public class HasIllegalConstructorCore
{
    public String foo;
    
    public HasIllegalConstructorCore(String foo) {
        this.foo = foo;
    }
}
