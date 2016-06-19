/*******************************************************************************
 * This file is part of the Panini project at Iowa State University.
 *
 * @PaniniJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * @PaniniJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with @PaniniJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributors:
 * 	Dr. Hridesh Rajan,
 * 	Dalton Mills,
 * 	David Johnston,
 * 	Trey Erenberger
 *******************************************************************************/

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
