package org.paninij.apt.util;

public interface ArtifactMaker
{

    void add(Artifact artifact);

    void makeAll();

    void close();
    
}
