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
 *******************************************************************************/
package org.paninij.proc.check.capsule;

import static javax.lang.model.type.TypeKind.ARRAY;
import static org.paninij.proc.check.Check.Result.OK;
import static org.paninij.proc.check.Check.Result.error;
import static org.paninij.proc.util.JavaModel.isAnnotatedBy;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 * Checks that all {@code @Local} fields are not signatures.
 */
public class CheckForLocalSignatureFields implements CapsuleCheck {
    private final ProcessingEnvironment procEnv;

    public CheckForLocalSignatureFields(ProcessingEnvironment procEnv) {
        this.procEnv = procEnv;
    }

    @Override
    public Result checkCapsule(TypeElement core) {
        for (Element element : core.getEnclosedElements()) {
            if (element.getKind() == ElementKind.FIELD) {
                if (hasLocalSignatureField(element)) {
                    String err = "Cannot have a `@Local` field whose type is a signature.";
                    return error(err, CheckForLocalSignatureFields.class, element);
                }
            }
        }
        return OK;
    }

    private boolean hasLocalSignatureField(Element field) {
        assert field.getKind() == ElementKind.FIELD;
        if (isAnnotatedBy(procEnv, field, "org.paninij.lang.Local")) {
            TypeMirror type = getScalarType(field.asType());
            if (type.getKind() == TypeKind.DECLARED) {
                TypeElement typeElem = (TypeElement) procEnv.getTypeUtils().asElement(type);
                if (typeElem == null) {
                    throw new IllegalArgumentException("Failed to lookup type element: " + type);
                }
                if (isAnnotatedBy(procEnv, typeElem, "org.paninij.lang.SignatureInterface")) {
                    return true;
                }
            }
        }
        return false;
    }

    private TypeMirror getScalarType(TypeMirror t) {
        return (t.getKind() != ARRAY) ? t : getScalarType(((ArrayType) t).getComponentType());
    }
}
