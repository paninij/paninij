package org.paninij.apt.util;

public class UserArtifact implements Artifact
{
    protected final String qualifiedName;
    
    public UserArtifact(String qualifiedName)
    {
        this.qualifiedName = qualifiedName;
    }

    @Override
    public String getQualifiedName()
    {
        return qualifiedName;
    }

    @Override
    public String getContent()
    {
        String msg = "The content for a  `UserArtifact` is unavailable.";
        throw new UnsupportedOperationException(msg);
    }
}
