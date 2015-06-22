package org.paninij.apt.maingen;

import org.paninij.apt.maingen.ActiveChild;
import org.paninij.apt.maingen.PassiveChild;
import org.paninij.apt.maingen.PassiveRoot;
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
