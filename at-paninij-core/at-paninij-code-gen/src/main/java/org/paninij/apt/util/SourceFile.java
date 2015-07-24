/*
 * This file is part of the Panini project at Iowa State University.
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributor(s): Dalton Mills
 */
package org.paninij.apt.util;
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
