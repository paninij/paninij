package org.paninij.soter.model;

import org.paninij.soter.util.WalaUtil;

public class Capsule
{
    String qualifiedName;
    
    /**
     * @param qualifiedName A fully-qualified capsule name of the form "org.paninij.examples.pi.Pi".
     *                      note that this does not expect the name of the capsule template.
     */
    public Capsule(String qualifiedName)
    {
        this.qualifiedName = qualifiedName;
    }
    
    public String getWalaPath()
    {
        return WalaUtil.fromQualifiedNameToWalaPath(qualifiedName);
    }
    
    public String getQualifiedName()
    {
        return qualifiedName;
    }
}
