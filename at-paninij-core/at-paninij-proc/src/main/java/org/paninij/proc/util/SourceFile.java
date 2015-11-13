
package org.paninij.proc.util;
/*
 * Basic helper class for holding information about a source file.
 * This is a class which can be returned to the AbstractProcessor to
 * be actually created.
 */
public class SourceFile implements Artifact
{
    public final String qualifiedName;
    public final String content;

    public SourceFile(String qualifiedName, String content) {
        this.qualifiedName = qualifiedName;
        this.content = content;
    }

    @Override
    public String toString() {
        return "FILENAME: " + this.qualifiedName + "\n CONTENT: \n" + this.content;
    }

    @Override
    public String getQualifiedName()
    {
        return qualifiedName;
    }
    
    @Override
    public String getContent()
    {
        return content;
    }
}
