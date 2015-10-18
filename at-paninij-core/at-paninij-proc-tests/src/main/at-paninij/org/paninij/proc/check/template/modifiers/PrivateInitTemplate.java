package org.paninij.proc.check.template.modifiers;

import org.paninij.lang.Capsule;
import org.paninij.proc.check.template.BadTemplate;

@BadTemplate
@Capsule
public class PrivateInitTemplate
{
    @SuppressWarnings("unused")
    private void init() {
        // Nothing.
    }
}
