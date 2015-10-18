package org.paninij.proc.check.template.toomany;

import org.paninij.lang.Capsule;
import org.paninij.proc.check.template.BadTemplate;

@BadTemplate
@Capsule
public class TooManyInitDeclsTemplate
{
    void init() {
        // Nothing.
    }

    void init(int i) {
        // Nothing.
    }
}
