package org.paninij.proc.maingen;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;
import org.paninij.lang.Imports;

@Capsule
public class PassiveLocalTemplate {

    @Local ActiveLocal c;
    @Imports PassiveRoot r;

    public void design(PassiveLocal self) {
        c.imports(self);
    }
}
