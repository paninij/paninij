package org.paninij.proc.check.template;

import static java.text.MessageFormat.format;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.STATIC;

import static org.paninij.proc.check.Result.ok;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;

import org.paninij.proc.check.Result;
import org.paninij.proc.check.Result.Error;


/**
 * Checks that a capsule template has either no init method or a single well-formed one.
 */
public class InitDeclCheck implements TemplateCheck
{
    public final static String ERROR_SOURCE = NoMainCheck.class.getName();
    public final static Modifier ILLEGAL_MODIFIERS[] = { STATIC, PRIVATE, PROTECTED };
    
    @Override
    public Result check(TypeElement template)
    {
        // Collect list casted references to each of the template's methods.
        List<ExecutableElement> methods = new ArrayList<ExecutableElement>();
        for (Element enclosed: template.getEnclosedElements()) {
            if (enclosed.getKind() == ElementKind.METHOD) {
                methods.add((ExecutableElement) enclosed);
            }
        }

        // Find the init method, or error if there is more than one.
        ExecutableElement init = null;
        for (ExecutableElement method: methods)
        {
            if (isNamedInit(method))
            {
                if (init == null) {
                    init = method;
                } else {
                    String err = "Capsule templates must contain zero or one `init()` methods, "
                               + "but two such methods were found in `{0}`.";
                    err = format(err, template.getSimpleName().toString());
                    return new Error(err, ERROR_SOURCE);
                }
            }
        }
        return (init == null) ? ok : check(template, init);
    }
    
    private Result check(TypeElement template, ExecutableElement init)
    {
        assert template != null && init != null;
        
        if (init.getReturnType().getKind() != TypeKind.VOID)
        {
            String err = "A capsule template's `init()` method must have void return type, "
                       + "but a non-void `init()` method was found in `{0}`.";
            err = format(err, template.getSimpleName());
            return new Error(err, ERROR_SOURCE);
        }

        if (!init.getTypeParameters().isEmpty())
        {
            String err = "A capsule template's `init()` method cannot have type paramters, "
                       + "but such a method was found in `{0}`.";
            err = format(err, template.getSimpleName());
            return new Error(err, ERROR_SOURCE);
        }

        for (Modifier illegalModifier : ILLEGAL_MODIFIERS)
        {
            if (init.getModifiers().contains(illegalModifier))
            {
                String err = "A capsule template's `init()` method cannot be declared `{0}`,"
                           + "but such a method was found in `{1}`.";
                err = format(err, illegalModifier.toString(), template.getSimpleName());
                return new Error(err, ERROR_SOURCE);
            }
        }
        
        return ok;
    }
    
    private static boolean isNamedInit(ExecutableElement exec)
    {
        return exec.getSimpleName().toString().equals("init");
    }
}
