package org.paninij.proc.check.capsule;

import static java.text.MessageFormat.format;

import static javax.lang.model.element.ElementKind.FIELD;
import static javax.lang.model.type.TypeKind.ARRAY;

import static org.paninij.proc.check.Result.ok;

import java.lang.annotation.Annotation;

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
import org.paninij.proc.check.CheckEnvironment;
import org.paninij.proc.check.Result;
import org.paninij.proc.check.Result.Error;


/**
 * Implements various checks related to a capsule template's fields, especially related to the
 * `@Local` and `@Imports` annotations.
 */
public class FieldsCheck implements CapsuleCheck
{
    private final static String ERROR_SOURCE = FieldsCheck.class.toString();
    private final CheckEnvironment env;

    public FieldsCheck(CheckEnvironment env) {
        this.env = env;
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
        
        return ok;
    }

    private Result checkField(TypeElement template, VariableElement field)
    {
        assert field.getKind() == FIELD;

        final boolean hasImports = hasAnnotation(field, Imports.class);
        final boolean hasLocal = hasAnnotation(field, Local.class);
        
        if (hasImports && hasLocal) {
            String err = "Found a field `{0} {1}` in `{2}` which is annotated with both `@Local`"
                       + "and `@Imports`.";
            err = format(err, field.asType(), field, template.getQualifiedName());
            return new Error(err, ERROR_SOURCE, field);
        }
        
        if (isCapsuleTemplateField(field))
        {
            String err = "Found a field `{0} {1}` in `{2}` which is a capsule template or a "
                       + "signature template. Use the generated capsule or signature instead.";
            err = format(err, field.asType(), field, template.getQualifiedName());
            return new Error(err, ERROR_SOURCE, field);
        }

        if (seemsToBeCapsuleField(field))
        {
            if (!hasImports && !hasLocal) {
                String err = "Found a field named `{0} {1}` in `{2}` whose type seems to be a "
                           + "capsule, but it is not annotated with either `@Local` or `@Imports`.";
                err = format(err, field.asType(), field, template.getQualifiedName());
                return new Error(err, ERROR_SOURCE, field);
            }
            
            if (isMultiDimensionalArrayField(field)) {
                String err = "Found a field named `{0} {1}` in `{2}` whose type seems to be a "
                           + "multi-dimensional array of capsule. This is not yet supported.";
                err = format(err, field.asType(), field, template.getQualifiedName());
                return new Error(err, ERROR_SOURCE, field);
            }
            
            // TODO: Check that @Local and @Imports fields don't have initializers.
        }
        else
        {
            if (hasLocal) {
                String err = "Found a field `{0} {1}` in `{2}` which is annotated with `@Local`, "
                           + "but its type seems to not be a capsule.";
                err = format(err, field.asType(), field, template.getQualifiedName());
                return new Error(err, ERROR_SOURCE, field);
            }
        }
        
        return ok;
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
            return (component.getKind() == ARRAY) ? true : false;
        } else {
            return false;
        }
    }

    private TypeElement lookupTypeElement(TypeMirror type)
    {
        TypeElement elem = (TypeElement) env.getTypeUtils().asElement(type);
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
