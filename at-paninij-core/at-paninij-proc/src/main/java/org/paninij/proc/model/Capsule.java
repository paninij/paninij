
package org.paninij.proc.model;

import java.util.List;

public interface Capsule extends Signature
{
    public List<Variable> getLocalFields();
    public List<Variable> getImportFields();
    public List<Variable> getStateFields();
    public List<String> getSignatures();
    public boolean isRoot();
    public boolean hasInit();
    public boolean hasRun();
    public boolean hasDesign();
    public boolean isActive();
    public boolean hasActiveAncestor();
}
