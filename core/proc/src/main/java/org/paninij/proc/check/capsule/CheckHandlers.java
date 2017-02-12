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

import static org.paninij.proc.check.Check.Result.OK;
import static org.paninij.proc.check.Check.Result.error;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;

import org.paninij.lang.Block;
import org.paninij.lang.Duck;
import org.paninij.lang.Future;
import org.paninij.lang.Handler;

/**
 * This checks that Handler methods are defined correctly.
 */
public class CheckHandlers implements CapsuleCheck {

    private static <A extends Annotation> boolean hasAnnotation(Element elem, Class<A> clazz) {
        A annotation = elem.getAnnotation(clazz);
        return annotation != null;
    }

    private Result checkMethod(ExecutableElement e) {
        if (!hasAnnotation(e, Handler.class)) {
            return OK;
        }

        if (hasAnnotation(e, Block.class)
                || hasAnnotation(e, Future.class)
                || hasAnnotation(e, Duck.class)) {
            String err = "A handler cannot have `@Block`, `@Future`, or `@Duck`.";
            return error(err, CheckHandlers.class, e);
        }

        if (e.getParameters().size() != 1) {
            String err = "A Handler must have exactly 1 parameter.";
            return error(err, CheckHandlers.class, e);
        }

        TypeKind paramKind = e.getParameters().get(0).asType().getKind();
        if (paramKind != TypeKind.DECLARED && paramKind != TypeKind.ERROR) {
            String err = "A handler cannot have primitive parameters.";
            return error(err, CheckHandlers.class, e);
        }
        
        if (e.getReturnType().getKind() != TypeKind.VOID) {
            String err = "A Handler may not return anything.";
            return error(err, CheckHandlers.class, e);
        }

        return OK;
    }

    @Override
    public Result checkCapsule(TypeElement core) {
        for (Element e : core.getEnclosedElements()) {
            if (e.getKind() == ElementKind.METHOD) {
                Result result = checkMethod((ExecutableElement) e);
                if (!result.ok()) {
                    return result;
                }
            }
        }
        return OK;
    }
}
