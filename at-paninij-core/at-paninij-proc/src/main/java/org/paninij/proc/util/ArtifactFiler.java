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

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileObject;

public class ArtifactFiler implements ArtifactMaker
{
    protected final Filer filer;

    protected final Set<Artifact> artifacts = new HashSet<Artifact>();


    public ArtifactFiler(Filer filer)
    {
        this.filer = filer;


    }
    
    @Override
    public void add(Artifact artifact)
    {
        if (artifact != null) {
            artifacts.add(artifact);
        }
    }

    @Override
    public void makeAll()
    {
        try
        {
            for (Artifact artifact : artifacts)
            {
                // Ignore any pre-made, user-defined artifacts.
                if (artifact instanceof UserArtifact)
                    continue;
                
                String qualifiedName = artifact.getQualifiedName();
                JavaFileObject sourceFile = filer.createSourceFile(qualifiedName);
                sourceFile.openWriter().append(artifact.getContent()).close();
            }
        }
        catch (IOException ex)
        {
            throw new RuntimeException("Failed to make all artifacts: " + ex, ex);
        }
        artifacts.clear();
    }
    
    @Override
    public void close()
    {
        // Nothing to do.
    }
}
