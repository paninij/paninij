package org.paninij.proc.check.capsule;

import static org.paninij.proc.check.Result.ok;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import org.paninij.lang.Imports;
import org.paninij.lang.Root;
import org.paninij.proc.check.Result;
import org.paninij.proc.check.Result.Error;

public class NoImportedFieldsOnRootCheck implements CapsuleCheck
{
    @Override
    public Result checkCapsule(TypeElement template) {
        if (hasAnnotation(template, Root.class)) {
            for (Element elem : template.getEnclosedElements()) {
                if (isImportField(elem)) {
                    String err = "Root capsules cannot have any `@Imports` fields.";
                    return new Error(err, NoImportedFieldsOnRootCheck.class, elem);
                }
            }
        }
        return ok;
    }
    
    private static boolean hasAnnotation(Element elem, Class<? extends Annotation> anno) {
        return elem.getAnnotation(anno) != null;
    }
    
    private static boolean isImportField(Element elem) {
        return elem.getKind() == ElementKind.FIELD
            && hasAnnotation(elem, Imports.class);
    }
}
