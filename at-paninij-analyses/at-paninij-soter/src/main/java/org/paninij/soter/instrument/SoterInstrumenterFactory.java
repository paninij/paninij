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
package org.paninij.soter.instrument;

import org.paninij.soter.SoterAnalysis;
import org.paninij.soter.model.CapsuleTemplate;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.ShrikeClass;
import com.ibm.wala.shrikeBT.analysis.ClassHierarchyStore;
import com.ibm.wala.shrikeBT.shrikeCT.ClassInstrumenter;
import com.ibm.wala.shrikeCT.InvalidClassFileException;

public class SoterInstrumenterFactory
{
    protected final String outputDir;
    
    public SoterInstrumenterFactory(String outputDir)
    {
        this.outputDir = outputDir;
    }

    public SoterInstrumenter make(SoterAnalysis sa)
    {
        CapsuleTemplate template = sa.getCapsuleTemplate();
        IClass templateClass = template.getTemplateClass();
        if (templateClass instanceof ShrikeClass == false) {
            String msg = "Could not cast the template's `IClass` to a `ShrikeClass`";
            throw new IllegalArgumentException(msg);
        }
        ShrikeClass templateShrike = (ShrikeClass) templateClass;
        
        try
        {
            ClassInstrumenter instrumenter = new ClassInstrumenter(template.getWalaPath(),
                                                                   templateShrike.getReader(),
                                                                   new ClassHierarchyStore(),
                                                                   false);
            return new SoterInstrumenter(template, outputDir, sa, instrumenter);
        }
        catch (InvalidClassFileException ex)
        {
            String msg = "Failed to make a `SoterInstrumenter`, because failed to make a needed "
                       + "`ClassInstrumenter` instance: " + ex;
            throw new IllegalArgumentException(msg);
        }
    }
}
