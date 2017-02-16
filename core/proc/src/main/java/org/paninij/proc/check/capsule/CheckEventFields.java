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
package org.paninij.proc.check.capsule;


import static javax.lang.model.element.ElementKind.FIELD;

import static org.paninij.proc.check.Check.Result.OK;
import static org.paninij.proc.check.Check.Result.error;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.paninij.lang.Broadcast;
import org.paninij.lang.Chain;
import org.paninij.lang.Imported;
import org.paninij.lang.Local;
import org.paninij.lang.Event;


/**
 * Implements various checks related to a capsule core's fields, especially related to the
 * `@Local` and `@Imported` annotations.
 */
public class CheckEventFields implements CapsuleCheck
{
    @Override
    public Result checkCapsule(TypeElement core)
    {
        for (Element elem: core.getEnclosedElements()) {
            if (elem.getKind() == FIELD) {
                Result result = checkField((VariableElement) elem);
                if (!result.ok()) {
                    return result;
                }
            }
        }

        return OK;
    }

    private Result checkField(VariableElement field)
    {
        final boolean hasBroadcast = hasAnnotation(field, Broadcast.class);
        final boolean hasChain = hasAnnotation(field, Chain.class);

        boolean nonEventType = false;
        TypeMirror mirror = field.asType();
        if (mirror.getKind() != TypeKind.DECLARED) {
            nonEventType = true;
        } else {
            String eventName = Event.class.getName();
            DeclaredType dec = (DeclaredType) mirror;
            TypeElement type = (TypeElement) dec.asElement();
            String fullTypeName = type.getQualifiedName().toString();

            nonEventType = !eventName.equals(fullTypeName);
        }
        
        if (nonEventType && (hasBroadcast || hasChain)) {
            String err = "Event type must be a Panini Event.";
            return error(err, CheckFields.class, field);
        }
         
        if (hasBroadcast && hasChain) {
            String err = "An event cannot be annotated with both `@Broadcast` and `@Chain`.";
            return error(err, CheckFields.class, field);
        }

        if (!nonEventType && (hasAnnotation(field, Imported.class) 
                || hasAnnotation(field, Local.class))) {
            String err = "An event cannot be annotated with `@Local` or `@Imported`";
            return error(err, CheckFields.class, field);
        }

        return OK;
    }

    private static <A extends Annotation> boolean hasAnnotation(Element elem, Class<A> clazz) {
        A annotation = elem.getAnnotation(clazz);
        return annotation != null;
    }
}
