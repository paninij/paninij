package org.paninij.apt;

import org.paninij.apt.model.Capsule;
import org.paninij.apt.util.SourceFile;

/**
 * An abstract class used to make `SourceFile` artifacts from a given `Capsule` model.
 */
public abstract class CapsuleArtifactFactory
{
    Capsule capsule;

    public SourceFile make(Capsule capsule)
    {
        this.capsule = capsule;
        return new SourceFile(this.getQualifiedName(), this.generateContent());
    }
 
    protected abstract String getQualifiedName();
    
    protected abstract String generateContent();
}
