package org.paninij.examples.maingen;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;
import org.paninij.lang.Wired;

@Capsule
public class PassiveChildTemplate {

    @Child ActiveChild c;
    @Wired PassiveRoot r;

    public void design(PassiveChild self) {
        c.wire(self);
    }
}
