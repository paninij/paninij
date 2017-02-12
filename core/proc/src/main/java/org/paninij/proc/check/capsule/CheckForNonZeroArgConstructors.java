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
package org.paninij.proc.check.capsule;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import static org.paninij.proc.check.Check.Result.error;

/**
 * Checks that any capsule core constructor only has zero arguments.
 */
public class CheckForNonZeroArgConstructors implements CapsuleCheck
{
    @Override
    public Result checkCapsule(TypeElement core)
    {
        // Ideally, this would be checking that there are no constructors except for the implicit
        // default constructor. However, I don't know how to use the `javax.lang.model` API to
        // differentiate between an implicit default constructor and some user-defined zero-arg
        // constructor.

        for (Element elem: core.getEnclosedElements())
        {
            if (elem.getKind() == ElementKind.CONSTRUCTOR)
            {
                int numParams = ((ExecutableElement) elem).getParameters().size();
                if (numParams > 0)
                {
                    String err = "A capsule core must not contain any constructors.";
                    return error(err, CheckForNonZeroArgConstructors.class, elem);
                }
            }
        }
        
        return Result.OK;
    } 
}
