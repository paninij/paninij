package org.paninij.apt.maingen;

import org.paninij.apt.maingen.PassiveChild;
import org.paninij.apt.maingen.PassiveRoot;
import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule
public class PassiveRootTemplate {

    @Child PassiveChild c;

    public void design(PassiveRoot self) {
        c.wire(self);
    }
}
