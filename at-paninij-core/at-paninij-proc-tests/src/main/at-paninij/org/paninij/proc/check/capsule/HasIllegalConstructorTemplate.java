package org.paninij.proc.check.capsule;

import org.paninij.lang.BadTemplate;
import org.paninij.lang.Capsule;

@BadTemplate
@Capsule
public class HasIllegalConstructorTemplate
{
    public String foo;
    
    public HasIllegalConstructorTemplate(String foo) {
        this.foo = foo;
    }
}
