package org.paninij.proc.factory;

import org.paninij.proc.util.SourceFile;

/**
 * A common interface for all artifact factories. An artifact factory makes `SourceFile` objects
 * from models of type `T`.
 */
public interface ArtifactFactory<T> {
    SourceFile make(T model);
}
