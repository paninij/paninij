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

import static org.paninij.proc.check.Result.ok;

import java.lang.annotation.Annotation;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.paninij.lang.Signature;
import org.paninij.lang.SignatureInterface;
import org.paninij.proc.check.CheckEnvironment;
import org.paninij.proc.check.Result;
import org.paninij.proc.check.Result.Error;

/**
 * Check that a template only implements interfaces which seem to be signature templates.
 */
public class ImplementedSignaturesCheck implements CapsuleCheck
{
    private final CheckEnvironment env;
    
    public ImplementedSignaturesCheck(CheckEnvironment env) {
        this.env = env;
    }
    
    @Override
    public Result checkCapsule(TypeElement template)
    {
        for (TypeMirror type : template.getInterfaces())
        {
            if (seemsToBeSignatureInterfaceType(type)) {
                String err = "A capsule template cannot implement signature interfaces. Implement "
                           + "the signature template instead.";
                return new Error(err, ImplementedSignaturesCheck.class, template);
            }

            if (!isSignatureTemplateType(type)) {
                String err = "A capsule template implements an interface which is not a signature "
                           + "template.";
                return new Error(err, ImplementedSignaturesCheck.class, template);
            }
        }
        return ok;
    }
    
    private boolean seemsToBeSignatureInterfaceType(TypeMirror type)
    {
        switch (type.getKind()) {
        case ERROR:
            // Optimistically interpret types which could not be found to be signature interfaces
            // that just haven't been created yet by `proc`.
            return true;
        case DECLARED:
            TypeElement elem = (TypeElement) env.getTypeUtils().asElement(type);
            return hasAnnotation(elem, SignatureInterface.class);
        default:
            return false;
        }
    }
    
    private boolean isSignatureTemplateType(TypeMirror type)
    {
        if (type.getKind() == TypeKind.DECLARED) {
            TypeElement elem = (TypeElement) env.getTypeUtils().asElement(type);
            return hasAnnotation(elem, Signature.class);
        } else {
            return false;
        }
    }
    
    private static <A extends Annotation> boolean hasAnnotation(TypeElement elem, Class<A> anno) {
        return elem.getAnnotation(anno) != null;
    }
}
