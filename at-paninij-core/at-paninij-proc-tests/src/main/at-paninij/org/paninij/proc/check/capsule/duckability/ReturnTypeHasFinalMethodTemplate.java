package org.paninij.proc.check.capsule.duckability;

import org.paninij.lang.BadTemplate;
import org.paninij.lang.Capsule;
import org.paninij.proc.check.duckability.ClassWithFinalMethod;

@BadTemplate
@Capsule
public class ReturnTypeHasFinalMethodTemplate
{
    public ClassWithFinalMethod proc()
    {
        // Nothing else needed.
        return null;
    }
}
