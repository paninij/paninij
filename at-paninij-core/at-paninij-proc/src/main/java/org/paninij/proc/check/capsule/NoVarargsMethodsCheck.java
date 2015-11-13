package org.paninij.proc.check.capsule;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import org.paninij.proc.check.Result;

/**
 * Checks that a capsule template does not contain a method which is variadic (i.e. has a variable
 * number of arguments).
 */
public class NoVarargsMethodsCheck implements CapsuleCheck
{
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
                    String err = "A capsule template must not contain a varargs method.";
                    return new Result.Error(err, NoVarargsMethodsCheck.class, execElem);
                }
            }
        }
        
        return Result.ok;
    } 
}
