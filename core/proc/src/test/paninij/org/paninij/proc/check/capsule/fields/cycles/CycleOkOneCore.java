package org.paninij.proc.check.capsule.fields.cycles;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;

@Capsule
class CycleOkOneCore {
    @Local CycleOkTwo two;
    void design(CycleOkOne self) {
        two.imports(self);
    }
}
