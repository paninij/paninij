
package org.paninij.proc.model;

import java.util.List;

public interface Procedure
{
    public abstract String getName();
    public abstract AnnotationKind getAnnotationKind();
    public abstract Type getReturnType();
    public abstract List<Variable> getParameters();
    public abstract List<String> getModifiers();
    public abstract List<String> getThrown();
}
