package org.paninij.proc.check.template.toomany;

import org.paninij.lang.Capsule;
import org.paninij.proc.check.template.BadTemplate;

@BadTemplate
@Capsule
public class TooManyRunDeclsTemplate
{
    void run() {
        // Nothing.
    }

    void run(int i) {
        // Nothing.
    }
}
