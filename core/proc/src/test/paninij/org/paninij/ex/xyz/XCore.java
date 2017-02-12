package org.paninij.ex.xyz;

import org.paninij.lang.Capsule;
import org.paninij.lang.CapsuleSystem;
import org.paninij.lang.Local;
import org.paninij.lang.Root;

@Root
@Capsule
public class XCore
{
    @Local Y y;

    public void design(X self) {
        y.imports(self);
    }
    
    public static void main(String[] args) {
        CapsuleSystem.start(X.class, args);
    }
}
