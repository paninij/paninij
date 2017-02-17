package org.paninij.proc.check.capsule;

import org.paninij.lang.Capsule;

@Capsule
class HasIllegalConstructorCore
{
    String foo;
    
    HasIllegalConstructorCore(String foo) {
        this.foo = foo;
    }
}
