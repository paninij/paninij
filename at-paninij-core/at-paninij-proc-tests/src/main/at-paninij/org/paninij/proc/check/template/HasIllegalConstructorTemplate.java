package org.paninij.proc.check.template;

import org.paninij.lang.Capsule;

@Capsule
public class HasIllegalConstructorTemplate
{
    public String foo;
    
    public HasIllegalConstructorTemplate(String foo) {
        this.foo = foo;
    }
}
