package org.paninij.proc.check.capsule;

import java.text.MessageFormat;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import org.paninij.proc.check.Result;

/**
 * Checks that a capsule template does not contain a method which is variadic (i.e. has a variable
 * number of arguments).
 */
public class NoVariadicMethodsCheck implements CapsuleCheck
{
    public static String errorSource = NoVariadicMethodsCheck.class.getName();

    @Override
    public Result checkCapsule(TypeElement template)
    {
        for (Element elem: template.getEnclosedElements())
        {
            // TODO: Notice that this performs the check on every method, not every procedure.
            if (elem.getKind() == ElementKind.METHOD)
            {
                ExecutableElement execElem = (ExecutableElement) elem;
                if (execElem.isVarArgs())
                {
                    String templateName = template.getSimpleName().toString();
                    String methodName = execElem.getSimpleName().toString();

                    String err = "A capsule template must not contain a variadic method (i.e. a "
                               + "method with a variable number of arguments), but such a method "
                               + "was found: `{0}.{1}(...)`";
                    err = MessageFormat.format(err, templateName, methodName);

                    return new Result.Error(err, errorSource);
                }
            }
        }
        
        return Result.ok;
    } 
}
