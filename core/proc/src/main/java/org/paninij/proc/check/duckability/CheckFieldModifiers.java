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
package org.paninij.proc.check.duckability;

import static java.text.MessageFormat.format;

import static javax.lang.model.element.ElementKind.FIELD;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;

import static org.paninij.proc.check.Check.Result.OK;
import static org.paninij.proc.check.Check.Result.error;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * Check that all fields of a type to be ducked have valid field modifiers (i.e. check that every
 * field is either `private` or `static`).
 */
public class CheckFieldModifiers implements DuckabilityCheck
{
    @Override
    public Result checkDuckability(TypeElement toDuck)
    {
        for (Element elem : toDuck.getEnclosedElements()) {
            if (elem.getKind() == FIELD && ! hasAnyModifier(elem, PRIVATE, STATIC)) {
                String err = "Type `{0}` has a field named `{1}` which is not declared either "
                           + "`private` or `static`.";
                err = format(err, toDuck, elem.getSimpleName());
                return error(err, CheckFieldModifiers.class, elem);
            }
        }
        return OK;
    }
    
    /**
     * @return true iff the given `elem` has any of the given modifiers.
     */
    private static boolean hasAnyModifier(Element elem, Modifier... modifiers) {
        for (Modifier m : modifiers) {
            if (elem.getModifiers().contains(m)) {
                return true;
            }
        }
        return false;
    }
}
