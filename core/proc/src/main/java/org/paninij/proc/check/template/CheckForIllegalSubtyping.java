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
package org.paninij.proc.check.template;

import static org.paninij.proc.check.Check.Result.OK;
import static org.paninij.proc.check.Check.Result.error;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import org.paninij.proc.check.capsule.CapsuleCheck;
import org.paninij.proc.check.signature.SignatureCheck;


/**
 * Checks that that a capsule template or signature template is not a subclass of anything except
 * `java.lang.Object`.
 */
public class CheckForIllegalSubtyping implements CapsuleCheck, SignatureCheck
{
    private final ProcessingEnvironment procEnv;

    public CheckForIllegalSubtyping(ProcessingEnvironment procEnv) {
        this.procEnv = procEnv;
    }

    @Override
    public Result checkSignature(TypeElement template) {
        List<? extends TypeMirror> interfaces = template.getInterfaces();
        if (!interfaces.isEmpty()) {
            String err = "A signature template must not be a subinterface.";
            return error(err, CheckForIllegalSubtyping.class, template);
        }
        return OK;
    }

    @Override
    public Result checkCapsule(TypeElement template) {
        TypeMirror object = procEnv.getElementUtils().getTypeElement("java.lang.Object").asType();
        if (! procEnv.getTypeUtils().isSameType(template.getSuperclass(), object)) {
            String err = "A capsule template must not extend anything except `java.lang.Object`.";
            return error(err, CheckForIllegalSubtyping.class, template);
        }
        return OK;
    }
}
