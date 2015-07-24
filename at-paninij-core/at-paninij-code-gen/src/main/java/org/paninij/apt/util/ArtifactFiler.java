package org.paninij.apt.util;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;

import org.paninij.apt.ProcessorOptions;

public class ArtifactFiler implements ArtifactMaker
{
    protected final Filer filer;
    protected final Set<Artifact> artifacts = new HashSet<Artifact>();

    private ArtifactFiler(Filer filer)
    {
        this.filer = filer;
    }
    
    public static ArtifactFiler make(Filer filer, ProcessorOptions options)
    {
        return new ArtifactFiler(filer);
    }

    @Override
    public void add(Artifact artifact)
    {
        if (artifact != null) {
            artifacts.add(artifact);
        }
    }

    @Override
    public void makeAll()
    {
        try
        {
            for (Artifact artifact : artifacts)
            {
                // Ignore any pre-made, user-defined artifacts.
                if (artifact instanceof UserArtifact)
                    continue;
                
                JavaFileObject sourceFile = filer.createSourceFile(artifact.getQualifiedName());
                sourceFile.openWriter().append(artifact.getContent()).close();
            }
        }
        catch (IOException ex)
        {
            throw new RuntimeException("Failed to make all artifacts: " + ex, ex);
        }
        artifacts.clear();
    }
}
