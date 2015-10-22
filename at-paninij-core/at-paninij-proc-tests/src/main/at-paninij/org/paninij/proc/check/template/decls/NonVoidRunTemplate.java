package org.paninij.proc.check.template.decls;

import org.paninij.lang.Capsule;
import org.paninij.proc.check.template.BadTemplate;

@BadTemplate
@Capsule
public class NonVoidRunTemplate
{
    protected Object run() {
        return null;
    }
}