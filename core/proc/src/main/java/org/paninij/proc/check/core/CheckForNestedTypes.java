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
package org.paninij.proc.check.core;

import static java.text.MessageFormat.format;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.INTERFACE;
import static javax.lang.model.element.ElementKind.ENUM;
import static javax.lang.model.element.ElementKind.ANNOTATION_TYPE;

import static org.paninij.proc.check.Check.Result.OK;
import static org.paninij.proc.check.Check.Result.error;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;


/**
 * Checks that a capsule core declaration does not declare any nested types, i.e. classes,
 * interfaces, enums, and annotations.
 */
public class CheckForNestedTypes implements CoreCheck
{
    public static final ElementKind[] ILLEGAL_NESTED_KINDS = {
        CLASS,
        INTERFACE,
        ENUM,
        ANNOTATION_TYPE
    };
    
    private static boolean isIllegalKind(ElementKind kind)
    {
        for (ElementKind illegal : ILLEGAL_NESTED_KINDS) {
            if (illegal == kind) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Result checkCore(TypeElement core, CoreKind coreKind)
    {
        for (Element enclosed : core.getEnclosedElements())
        {
            ElementKind kind = enclosed.getKind();
            if (isIllegalKind(kind)) {
                String err = "A {0} core must not contain a nested {1}.";
                err = format(err, coreKind, kind, enclosed.getSimpleName(),
                                  core.getSimpleName());
                return error(err, CheckForNestedTypes.class, enclosed);
            }
        }

        return OK;
    }
}
