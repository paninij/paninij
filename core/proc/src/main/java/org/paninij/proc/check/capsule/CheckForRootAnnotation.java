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

import static org.paninij.proc.check.Check.Result.OK;
import static org.paninij.proc.check.Check.Result.error;

import javax.lang.model.element.TypeElement;

import org.paninij.lang.Root;
import org.paninij.proc.check.signature.SignatureCheck;

/**
 * Check that a signature template is not annotated with {@code @Root}.
 */
public class CheckForRootAnnotation implements SignatureCheck
{
    @Override
    public Result checkSignature(TypeElement template) {
        if (template.getAnnotation(Root.class) == null) {
            return OK;
        } else {
            String err = "A signature template must not be annotated with `@Root`.";
            return error(err, CheckForRootAnnotation.class, template);
        }
    }
}
