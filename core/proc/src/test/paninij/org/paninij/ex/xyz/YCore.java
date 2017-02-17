package org.paninij.ex.xyz;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;
import org.paninij.lang.Imported;

@Capsule
class YCore
{
    @Imported X x;
    @Local Z z;

    void design(Y self) {
        z.imports(x, self);
    }
}
