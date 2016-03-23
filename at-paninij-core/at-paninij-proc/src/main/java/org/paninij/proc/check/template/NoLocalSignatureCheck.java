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
package org.paninij.proc.check.template;

import static javax.lang.model.type.TypeKind.ARRAY;
import static org.paninij.proc.check.Result.ok;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.paninij.lang.Local;
import org.paninij.lang.SignatureInterface;
import org.paninij.proc.check.CheckEnvironment;
import org.paninij.proc.check.Result;

public class NoLocalSignatureCheck extends AbstractTemplateCheck {
    private final CheckEnvironment env;

    public NoLocalSignatureCheck(CheckEnvironment env) {
        this.env = env;
    }

    @Override
    protected Result checkTemplate(TemplateKind templateKind, TypeElement template) {
        for (Element element : template.getEnclosedElements()) {
            if (element.getKind() == ElementKind.FIELD) {
                if (!checkLocalField(element)) {
                    String err = "Cannot have `@Local` for a Signature typed field.";
                    return new Result.Error(err, NoLocalSignatureCheck.class, element);
                }
            }
        }
        return ok;
    }

    private boolean checkLocalField(Element field) {
        assert field.getKind() == ElementKind.FIELD;

        if (field.getAnnotation(Local.class) == null) {
            return true;
        }

        TypeMirror type = getScalarType(field.asType());
        if (type.getKind() == TypeKind.DECLARED) {
            TypeElement elem = (TypeElement) env.getTypeUtils().asElement(type);
            if (elem == null) {
                throw new IllegalArgumentException("Failed to lookup type element for " + type);
            }
            if (elem.getAnnotation(SignatureInterface.class) != null) {
                return false;
            }
        }
        return true;
    }

    private TypeMirror getScalarType(TypeMirror t) {
        return (t.getKind() != ARRAY) ? t : getScalarType(((ArrayType) t).getComponentType());
    }
}
