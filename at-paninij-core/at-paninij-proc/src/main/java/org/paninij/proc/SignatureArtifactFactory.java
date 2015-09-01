package org.paninij.proc;

import org.paninij.proc.model.Signature;
import org.paninij.proc.util.SourceFile;

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
