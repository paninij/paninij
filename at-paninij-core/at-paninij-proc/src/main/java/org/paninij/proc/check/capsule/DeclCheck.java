package org.paninij.proc.check.capsule;

import static java.text.MessageFormat.format;

import static org.paninij.proc.check.Result.ok;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;

import org.paninij.proc.check.Check;
import org.paninij.proc.check.Result;
import org.paninij.proc.check.Result.Error;


/**
 * An abstract class extended by checks. Subclasses are meant to check whether one of a capsule
 * template's declarations is well-formed one. (`NotTooManyDeclsCheck` should probably be run before
 * running any checks which extend this class.)
 */
public abstract class DeclCheck implements CapsuleCheck
{
    public abstract Class<? extends Check> getErrorSource();
    
    public abstract String getDeclName();
    
    public abstract boolean hasValidParameters(TypeElement template, ExecutableElement decl);

    
    @Override
    public Result checkCapsule(TypeElement template)
    {
        // Collect list of the casted references to the template's methods.
        List<ExecutableElement> methods = new ArrayList<ExecutableElement>();
        for (Element enclosed: template.getEnclosedElements()) {
            if (enclosed.getKind() == ElementKind.METHOD) {
                methods.add((ExecutableElement) enclosed);
            }
        }

        // Find the init method if there is one.
        ExecutableElement decl = null;
        for (ExecutableElement method: methods) {
            if (method.getSimpleName().toString().equals(getDeclName())) {
                decl = method;
            }
        }
        return (decl == null) ? ok : check(template, decl);
    }

    
    private Result check(TypeElement template, ExecutableElement init)
    {
        assert template != null && init != null;
        
        if (init.getReturnType().getKind() != TypeKind.VOID) {
            String err = "A {0} declaration must have `void` return type.";
            err = format(err, getDeclName());
            return new Error(err, getErrorSource(), init);
        }
        
        if (!hasValidParameters(template, init)) {
            // TODO: Make this message more specific and instructive for the user.
            String err = "A {0} declaration has invalid parameters.";
            err = format(err, getDeclName(), template.getSimpleName());
            return new Error(err, getErrorSource(), init);
        }

        if (!init.getTypeParameters().isEmpty()) {
            String err = "A {0} declaration cannot have type parameters.";
            err = format(err, getDeclName());
            return new Error(err, getErrorSource(), init);
        }

        return ok;
    }
}
