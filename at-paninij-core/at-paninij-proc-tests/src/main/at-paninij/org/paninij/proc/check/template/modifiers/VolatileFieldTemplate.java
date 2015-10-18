package org.paninij.proc.check.template.modifiers;

import org.paninij.lang.Capsule;
import org.paninij.proc.check.template.BadTemplate;

@BadTemplate
@Capsule
public class VolatileFieldTemplate
{
    @SuppressWarnings("unused")
    private volatile int field = 0;
}
