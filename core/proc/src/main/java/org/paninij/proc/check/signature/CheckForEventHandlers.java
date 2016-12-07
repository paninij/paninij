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
 *  Dr. Hridesh Rajan,
 *  Dalton Mills,
 *  David Johnston,
 *  Trey Erenberger
 *  Jackson Maddox
 *******************************************************************************/
package org.paninij.proc.check.signature;

import static java.text.MessageFormat.format;

import static javax.lang.model.element.ElementKind.METHOD;

import static org.paninij.proc.check.Check.Result.OK;
import static org.paninij.proc.check.Check.Result.error;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import org.paninij.lang.Handler;

/**
 * This class ensures that signatures do not have any Handler methods.
 */
public class CheckForEventHandlers implements SignatureCheck
{
    private String hasEventAnnotation(Element e) {
        if (e.getAnnotation(Handler.class) != null) {
            return "Handler";
        }
        return null;
    }

    @Override
    public Result checkSignature(TypeElement signature)
    {
        for (Element e : signature.getEnclosedElements()) {
            if (e.getKind() != METHOD) {
                continue;
            }

            String illegalAnnotation = hasEventAnnotation(e);
            if (illegalAnnotation != null) {
                String err = "The signature method `{0}()` has illegal annotation `{1}`";
                err = format(err, e.getSimpleName(), illegalAnnotation);
                return error(err, CheckForllegalNames.class, e);
            }
        }
        return OK;
    }
}
