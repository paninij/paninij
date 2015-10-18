package org.paninij.proc.check.template;

import static java.text.MessageFormat.format;

import static org.paninij.proc.check.Result.ok;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;

import org.paninij.proc.check.Result;
import org.paninij.proc.check.Result.Error;


/**
 * Checks that a capsule template has either no init method or a single well-formed one.
 */
public class InitDeclCheck implements TemplateCheck
{
    public final static String ERROR_SOURCE = InitDeclCheck.class.getName();

    
    @Override
    public Result check(TypeElement template)
    {
        // Collect list of the casted references to the template's methods.
        List<ExecutableElement> methods = new ArrayList<ExecutableElement>();
        for (Element enclosed: template.getEnclosedElements()) {
            if (enclosed.getKind() == ElementKind.METHOD) {
                methods.add((ExecutableElement) enclosed);
            }
        }

        // Find the init method if there is one.
        ExecutableElement init = null;
        for (ExecutableElement method: methods) {
            if (isNamedInit(method)) {
                init = method;
            }
        }
        return (init == null) ? ok : check(template, init);
    }
    
    private Result check(TypeElement template, ExecutableElement init)
    {
        assert template != null && init != null;
        
        if (init.getReturnType().getKind() != TypeKind.VOID)
        {
            String err = "The `init()` method of a capsule template must have void return type, "
                       + "but a non-void `init()` method was found in `{0}`.";
            err = format(err, template.getSimpleName());
            return new Error(err, ERROR_SOURCE);
        }
        
        if (!init.getParameters().isEmpty())
        {
            String err = "The `init()` method of a capsule template cannot have any parameters, "
                       + "but such a method was found in `{0}`.";
            err = format(err, template.getSimpleName());
            return new Error(err, ERROR_SOURCE);
        }

        if (!init.getTypeParameters().isEmpty())
        {
            String err = "The `init()` method of a capsule template cannot have type parameters, "
                       + "but such a method was found in `{0}`.";
            err = format(err, template.getSimpleName());
            return new Error(err, ERROR_SOURCE);
        }

        return ok;
    }
    
    private static boolean isNamedInit(ExecutableElement exec)
    {
        return exec.getSimpleName().toString().equals("init");
    }
}
