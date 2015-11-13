
package org.paninij.proc.model;

import java.util.List;
import java.util.Set;

public interface Signature
{
    public String getSimpleName();

    public String getQualifiedName();

    public String getPackage();

    public Set<String> getImports();

    public List<Procedure> getProcedures();
}
