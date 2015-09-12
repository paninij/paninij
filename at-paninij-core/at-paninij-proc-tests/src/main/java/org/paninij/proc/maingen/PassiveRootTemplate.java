package org.paninij.proc.maingen;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;

@Capsule
public class PassiveRootTemplate {

    @Local PassiveLocal c;

    public void design(PassiveRoot self) {
        c.imports(self);
    }
}
