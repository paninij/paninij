package org.paninij.proc.activepassive;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;
import org.paninij.lang.Imports;

@Capsule
public class YTemplate
{
    @Imports X x;
    @Local Z z;

    public void design(Y self) {
        z.imports(x, self);
    }
}
