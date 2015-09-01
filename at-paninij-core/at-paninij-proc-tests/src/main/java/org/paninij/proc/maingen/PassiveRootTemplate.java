package org.paninij.proc.maingen;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule
public class PassiveRootTemplate {

    @Child PassiveChild c;

    public void design(PassiveRoot self) {
        c.wire(self);
    }
}
