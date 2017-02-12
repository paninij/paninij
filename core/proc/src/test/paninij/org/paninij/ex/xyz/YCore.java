package org.paninij.ex.xyz;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;
import org.paninij.lang.Imports;

@Capsule
public class YCore
{
    @Imports X x;
    @Local Z z;

    public void design(Y self) {
        z.imports(x, self);
    }
}
