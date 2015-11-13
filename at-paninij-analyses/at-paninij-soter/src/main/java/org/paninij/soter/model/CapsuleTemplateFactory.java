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
package org.paninij.soter.model;

import static com.ibm.wala.types.ClassLoaderReference.Application;

import org.paninij.soter.util.WalaUtil;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.types.TypeReference;

public class CapsuleTemplateFactory
{
    IClassHierarchy cha;

    public CapsuleTemplateFactory(IClassHierarchy cha)
    {
        this.cha = cha;
    }
    
    /**
     * @param capsuleTemplate A fully qualified name of a capsule (e.g. "org.paninij.examples.pi.Pi").
     */
    public CapsuleTemplate make(String capsuleName)
    {
        String templatePath = WalaUtil.fromQualifiedNameToWalaPath(capsuleName) + "Template";

        TypeReference templateReference = TypeReference.find(Application, templatePath);
        if (templateReference == null)
        {
            String msg = "Could not find the `TypeReference` for template: " + templatePath;
            throw new RuntimeException(msg);
        }
        
        IClass templateClass = cha.lookupClass(templateReference);
        if (templateClass == null)
        {
            String msg = "Could not find the `IClass` for template: " + templatePath;
            throw new RuntimeException(msg);
        }

        return new CapsuleTemplate(templateClass);
    }
}
