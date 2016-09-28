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

import static java.text.MessageFormat.format;

import static javax.lang.model.element.Modifier.DEFAULT;
import static javax.lang.model.element.Modifier.STATIC;

import static org.paninij.proc.check.Check.Result.OK;
import static org.paninij.proc.check.Check.Result.error;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

public class CheckForIllegalModifiers implements SignatureCheck
{
    private static final Modifier[] ILLEGAL_MODIFIERS = {
        DEFAULT,
        STATIC,
    };
    
    private static boolean isIllegalModifier(Modifier m)
    {
        for (Modifier illegal : ILLEGAL_MODIFIERS) {
            if (m == illegal) {
                return true;
            }
        }
        return false;
    }
    
    private static Modifier getIllegalModifierIfAny(ExecutableElement method)
    {
        for (Modifier m : method.getModifiers()) {
            if (isIllegalModifier(m)) {
                return m;
            }
        }
        return null;
    }

    @Override
    public Result checkSignature(TypeElement signature)
    {
        for (Element e : signature.getEnclosedElements()) {
            if (e.getKind() == ElementKind.METHOD) {
                Modifier illegalModifier = getIllegalModifierIfAny((ExecutableElement) e);
                if (illegalModifier != null) {
                    String err = "A signature template method includes an illegal modifier: {0}.";
                    err = format(err, illegalModifier);
                    return error(err, CheckForIllegalModifiers.class, e);
                }
            }
        }
        return OK;
    }
}
