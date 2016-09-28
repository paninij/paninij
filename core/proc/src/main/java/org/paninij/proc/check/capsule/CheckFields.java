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

import static java.text.MessageFormat.format;

import static javax.lang.model.element.ElementKind.FIELD;
import static javax.lang.model.type.TypeKind.ARRAY;

import static org.paninij.proc.check.Check.Result.OK;
import static org.paninij.proc.check.Check.Result.error;

import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeMirror;

import org.paninij.lang.Capsule;
import org.paninij.lang.CapsuleInterface;
import org.paninij.lang.Imports;
import org.paninij.lang.Local;
import org.paninij.lang.Signature;
import org.paninij.lang.SignatureInterface;


/**
 * Implements various checks related to a capsule template's fields, especially related to the
 * `@Local` and `@Imports` annotations.
 */
public class CheckFields implements CapsuleCheck
{
    private final ProcessingEnvironment procEnv;

    public CheckFields(ProcessingEnvironment procEnv) {
        this.procEnv = procEnv;
    }

    @Override
    public Result checkCapsule(TypeElement template)
    {
        for (Element elem: template.getEnclosedElements()) {
            if (elem.getKind() == FIELD) {
                Result result = checkField(template, (VariableElement) elem);
                if (!result.ok()) {
                    return result;
                }
            }
        }
        
        return OK;
    }

    private Result checkField(TypeElement template, VariableElement field)
    {
        assert field.getKind() == FIELD;

        final boolean hasImports = hasAnnotation(field, Imports.class);
        final boolean hasLocal = hasAnnotation(field, Local.class);
        
        if (hasImports && hasLocal) {
            String err = "A field cannot be annotated with both `@Local` and `@Imports`.";
            return error(err, CheckFields.class, field);
        }
        
        if (isCapsuleTemplateField(field))
        {
            String err = "Found a field whose type is a capsule or signature template. Use the "
                       + "generated capsule or signature interface instead.";
            return error(err, CheckFields.class, field);
        }

        if (seemsToBeCapsuleField(field))
        {
            if (!hasImports && !hasLocal) {
                String err = "Found a field whose type seems to be a capsule, but it is not "
                           + "annotated with either `@Local` or `@Imports`.";
                return error(err, CheckFields.class, field);
            }
            
            if (isMultiDimensionalArrayField(field)) {
                String err = "Found a field whose type seems to be a multi-dimensional array of "
                           + "capsules or signatures. This is not yet supported.";
                return error(err, CheckFields.class, field);
            }
            
            // TODO: Check that @Local and @Imports fields don't have initializers.
        }
        else
        {
            if (hasLocal) {
                String err = "Found a field annotated with `@Local`, but its type seems to not be "
                           + "a capsule.";
                err = format(err, field.asType(), field, template.getQualifiedName());
                return error(err, CheckFields.class, field);
            }
        }
        
        return OK;
    }
    
    private boolean isCapsuleTemplateField(VariableElement field)
    {
        assert field.getKind() == FIELD;
        
        // Make decisions based on the scalar type.
        TypeMirror type = getScalarType(field.asType());
        switch (type.getKind()) {
        case DECLARED:
            TypeElement elem = lookupTypeElement(type);
            return hasAnnotation(elem, Capsule.class) || hasAnnotation(elem, Signature.class); 
        default:
            return false;
        }
    }

    private boolean seemsToBeCapsuleField(VariableElement field)
    {
        assert field.getKind() == FIELD;
        
        // Make decision based on scalar type:
        TypeMirror type = getScalarType(field.asType());
        switch (type.getKind()) {
        case ERROR:
            // When the field's type kind is `ERROR` it means that we cannot retrieve a field's
            // fully qualified type. This is either because the user has has incorrectly used some
            // unavailable type or (more likely) the field's type has not yet been generated by
            // `proc`. Here we just assume that all such fields are of capsule type.
            return true;
        case DECLARED:
            TypeElement elem = lookupTypeElement(type);
            return hasAnnotation(elem, CapsuleInterface.class)
                || hasAnnotation(elem, SignatureInterface.class);
        default:
            return false;
        }
    }
    
    private TypeMirror getScalarType(TypeMirror t) {
        return (t.getKind() != ARRAY) ? t : getScalarType(((ArrayType) t).getComponentType());
    }
    
    private boolean isMultiDimensionalArrayField(VariableElement field)
    {
        assert field.getKind() == FIELD;
        TypeMirror typeMirror = field.asType();

        if (typeMirror.getKind() == ARRAY) {
            // See whether the array's components are of array type.
            TypeMirror component = ((ArrayType) typeMirror).getComponentType();
            return component.getKind() == ARRAY;
        } else {
            return false;
        }
    }

    private TypeElement lookupTypeElement(TypeMirror type)
    {
        TypeElement elem = (TypeElement) procEnv.getTypeUtils().asElement(type);
        if (elem == null) {
            throw new IllegalArgumentException("Failed to lookup type element for " + type);
        }
        return elem;
    }
    
    private static <A extends Annotation> boolean hasAnnotation(Element elem, Class<A> clazz) {
        A annotation = elem.getAnnotation(clazz);
        return annotation != null;
    }
}
