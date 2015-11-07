package org.paninij.proc.check.capsule;

import static java.text.MessageFormat.format;

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
    private static final String ERROR_SOURCE = ImplementedSignaturesCheck.class.getName();
    
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
                String err = "Capsule template `{0}` implements the interface `{1}`. This type "
                           + "seems to be a signature interface, but capsule templates should only "
                           + "implement signature templates.";
                err = format(err, template.getQualifiedName(), type.toString());
                return new Error(err, ERROR_SOURCE, template);
            }

            if (!isSignatureType(type)) {
                String err = "Capsule template `{0}` implements the interface `{1}`, but that type "
                           + "does not seem to correspond to a signature.";
                err = format(err, template.getQualifiedName(), type.toString());
                return new Error(err, ERROR_SOURCE, template);
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
    
    private boolean isSignatureType(TypeMirror type)
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
