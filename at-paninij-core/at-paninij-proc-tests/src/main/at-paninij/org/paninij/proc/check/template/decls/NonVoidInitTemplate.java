package org.paninij.proc.check.template.decls;

import org.paninij.lang.Capsule;
import org.paninij.proc.check.template.BadTemplate;

@BadTemplate
@Capsule
public class NonVoidInitTemplate
{
    protected Object init() {
        return null;
    }
}
