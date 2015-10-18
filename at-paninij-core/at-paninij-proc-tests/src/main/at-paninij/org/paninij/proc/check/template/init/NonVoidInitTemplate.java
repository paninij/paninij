package org.paninij.proc.check.template.init;

import org.paninij.lang.Capsule;
import org.paninij.proc.check.template.BadTemplate;

@BadTemplate
@Capsule
public class NonVoidInitTemplate
{
    protected Object init(int i) {
        return null;
    }
}
