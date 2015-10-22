package org.paninij.proc.check.template.modifiers;

import org.paninij.lang.Capsule;
import org.paninij.proc.check.template.BadTemplate;

@BadTemplate
@Capsule
public class SynchronizedMethodTemplate
{
    public synchronized void procA()
    {
        // Nothing needed here.
    }
}
