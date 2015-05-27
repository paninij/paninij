package org.paninij.apt.util;
/*
 * Basic helper class for holding information about a source file.
 * This is a class which can be returned to the AbstractProcessor to
 * be actually created.
 */
public class SourceFile
{
    public final String filename;
    public final String content;

    public SourceFile(String filename, String content) {
        this.filename = filename;
        this.content = content;
    }

    @Override
    public String toString() {
        return "FILENAME: " + this.filename + "\n CONTENT: \n" + this.content;
    }

}
