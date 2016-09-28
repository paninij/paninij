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

import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.PUBLIC;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;

import static org.paninij.proc.check.Check.Result.OK;
import static org.paninij.proc.check.Check.Result.error;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;


/**
 * If the given template has a run declaration (i.e. some method named "run"), then the template is
 * interpreted to represent an active capsule. Otherwise it is interpreted to be a passive capsule.
 * If given an active capsule, then this check ensures that the template has no procedures.
 * Otherwise, if given a passive capsule, this check ensures that every procedure is well-formed.
 */
public class CheckProcedures implements CapsuleCheck
{
    private final static String[] DECLARATION_NAMES = {
        "init",
        "design",
        "run",
    };

    @Override
    public Result checkCapsule(TypeElement template) {
        return isActive(template) ? checkActiveTemplate(template) : checkPassiveTemplate(template);
    }
    
    private static boolean isActive(TypeElement template)
    {
        for (Element elem: template.getEnclosedElements()) {
            if (elem.getKind() == METHOD && elem.getSimpleName().toString().equals("run")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check that there are no procedures.
     */
    private Result checkActiveTemplate(TypeElement template)
    {
        for (Element elem: template.getEnclosedElements()) {
            if (isProcedure(elem)) {
                String err = "An active template cannot have any procedures.";
                return error(err, CheckProcedures.class, elem);
            }
        }
        return OK;
    }

    /**
     * Check that every procedure defined on the given passive capsule is well-formed.
     */
    private Result checkPassiveTemplate(TypeElement template)
    {
        for (Element elem: template.getEnclosedElements()) {
            if (isProcedure(elem)) {
                Result result = checkProcedure(template, (ExecutableElement) elem);
                if (!result.ok()) {
                    return result;
                }
            }
        }
        return OK;
    }
    
    /**
     * Check that a single procedure defined on the given passive capsule is well-formed.
     */
    private Result checkProcedure(TypeElement template, ExecutableElement procedure)
    {
        if (!procedure.getModifiers().contains(PUBLIC)) {
            String err = "A procedure must be declared `public`.";
            return error(err, CheckProcedures.class, procedure);
        }
        return OK;
    }
    
    /**
     * @return  `true` iff the given element represents a procedure (i.e. it is a method which is
     *          not `static`, not `private`, and not some kind of `@PaniniJ` declaration).
     */
    private boolean isProcedure(Element elem)
    {
        if (elem.getKind() != METHOD) {
            return false;
        }
        if (elem.getModifiers().contains(STATIC) || elem.getModifiers().contains(PRIVATE)) {
            return false;
        }
        for (String name : DECLARATION_NAMES) {
            if (elem.getSimpleName().toString().equals(name)) {
                return false;
            }
        }
        return true;
    }
}
