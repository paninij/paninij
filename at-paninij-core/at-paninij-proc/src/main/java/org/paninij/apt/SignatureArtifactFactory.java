package org.paninij.apt;

import org.paninij.apt.model.Signature;
import org.paninij.apt.util.SourceFile;

public abstract class SignatureArtifactFactory
{
    Signature signature;

    public SourceFile make(Signature signature)
    {
        this.signature = signature;
        return new SourceFile(this.getQualifiedName(), this.generateContent());
    }
 
    protected abstract String getQualifiedName();
    
    protected abstract String generateContent();
}
