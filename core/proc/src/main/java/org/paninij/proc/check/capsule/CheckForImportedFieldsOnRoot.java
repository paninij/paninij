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

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import org.paninij.lang.Imports;
import org.paninij.lang.Root;

public class CheckForImportedFieldsOnRoot implements CapsuleCheck
{
    @Override
    public Result checkCapsule(TypeElement core) {
        if (hasAnnotation(core, Root.class)) {
            for (Element elem : core.getEnclosedElements()) {
                if (isImportField(elem)) {
                    String err = "A root capsule cannot have any `@Imports` fields.";
                    return error(err, CheckForImportedFieldsOnRoot.class, elem);
                }
            }
        }
        return OK;
    }
    
    private static boolean hasAnnotation(Element elem, Class<? extends Annotation> anno) {
        return elem.getAnnotation(anno) != null;
    }
    
    private static boolean isImportField(Element elem) {
        return elem.getKind() == ElementKind.FIELD
            && hasAnnotation(elem, Imports.class);
    }
}
