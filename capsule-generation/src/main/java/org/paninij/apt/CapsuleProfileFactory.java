package org.paninij.apt;

import org.paninij.apt.util.SourceFile;
import org.paninij.model.Capsule;

public abstract class CapsuleProfileFactory
{
    public abstract SourceFile make(Capsule capsule);
}
