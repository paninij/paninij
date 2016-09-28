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
package org.paninij.proc.check.signature;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import org.paninij.proc.check.Check;

import static java.text.MessageFormat.format;
import static org.paninij.proc.check.Check.Result.error;

public interface SignatureCheck extends Check
{
    default Result checkSignature(Element elem) {
        if (elem.getKind() != ElementKind.INTERFACE) {
            String err = "A signature template must be an interface, but an element annotated with "
                    + "`@Signature` has TypeKind {0}.";
            err = format(err, elem.getKind());
            return error(err, AllSignatureChecks.class, elem);
        }
        return checkSignature((TypeElement) elem);
    }

    Result checkSignature(TypeElement template);
}
