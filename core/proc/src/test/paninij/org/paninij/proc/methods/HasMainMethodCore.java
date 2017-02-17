package org.paninij.proc.methods;

import org.paninij.lang.Capsule;
import org.paninij.lang.CapsuleSystem;
import org.paninij.lang.Root;

@Root @Capsule
class HasMainMethodCore {
    public static void main(String[] args) {
        CapsuleSystem.start(HasMainMethod.class, args);
    }
}
