package org.paninij.proc.factory;

import org.paninij.proc.model.Capsule;
import org.paninij.proc.util.SourceFile;

/**
 * An abstract class used to make `SourceFile` artifacts from a given `Capsule` model.
 */
public abstract class AbstractCapsuleFactory implements ArtifactFactory<Capsule>
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
