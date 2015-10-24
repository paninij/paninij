package org.paninij.proc.check.capsule;

import static java.text.MessageFormat.format;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.INTERFACE;
import static javax.lang.model.element.ElementKind.ENUM;
import static javax.lang.model.element.ElementKind.ANNOTATION_TYPE;

import static org.paninij.proc.check.Result.ok;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import org.paninij.proc.check.Result;
import org.paninij.proc.check.Result.Error;


/**
 * Checks that a capsule template declaration does not declare any nested types, i.e. classes,
 * interfaces, enums, and annotations.
 */
public class NoNestedTypesCheck implements CapsuleCheck
{
    public final static String ERROR_SOURCE = NoNestedTypesCheck.class.getName();
    
    public static final ElementKind[] ILLEGAL_NESTED_KINDS = {
        CLASS,
        INTERFACE,
        ENUM,
        ANNOTATION_TYPE
    };
    
    private static boolean isIllegalKind(ElementKind kind)
    {
        for (ElementKind illegal : ILLEGAL_NESTED_KINDS) {
            if (illegal == kind) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Result checkCapsule(TypeElement template)
    {
        for (Element enclosed : template.getEnclosedElements())
        {
            ElementKind kind = enclosed.getKind();
            if (isIllegalKind(kind)) {
                String err = "Capsule templates must not contain a nested {0}, but one named `{1}` "
                           + "was found in `{2}`.";
                err = format(err, kind, enclosed.getSimpleName(), template.getSimpleName());
                return new Error(err, ERROR_SOURCE);
            }
        }

        return ok;
    }
}
