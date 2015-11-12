package org.paninij.proc.check.capsule;

import java.text.MessageFormat;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import org.paninij.proc.check.Result;

/**
 * Checks that any capsule template constructor only has zero arguments.
 */
public class OnlyZeroArgConstructorsCheck implements CapsuleCheck
{
    @Override
    public Result checkCapsule(TypeElement template)
    {
        // Ideally, this would be checking that there are no constructors except for the implicit
        // default constructor. However, I don't know how to use the `javax.lang.model` API to
        // differentiate between an implicit default constructor and some user-defined zero-arg
        // constructor.

        for (Element elem: template.getEnclosedElements())
        {
            if (elem.getKind() == ElementKind.CONSTRUCTOR)
            {
                int numParams = ((ExecutableElement) elem).getParameters().size();
                if (numParams > 0)
                {
                    String err = "A capsule template must not contain any constructors, but a "
                               + "constructor was found in `{0}`.";
                    err = MessageFormat.format(err, template.getQualifiedName().toString());
                    return new Result.Error(err, OnlyZeroArgConstructorsCheck.class, elem);
                }
            }
        }
        
        return Result.ok;
    } 
}
