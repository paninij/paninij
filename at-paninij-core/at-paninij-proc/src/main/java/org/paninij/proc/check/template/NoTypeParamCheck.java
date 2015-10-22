package org.paninij.proc.check.template;

import static java.text.MessageFormat.format;

import static org.paninij.proc.check.Result.ok;

import javax.lang.model.element.TypeElement;

import org.paninij.proc.check.Result;
import org.paninij.proc.check.Result.Error;


public class NoTypeParamCheck implements TemplateCheck
{
    public final static String ERROR_SOURCE = NoTypeParamCheck.class.getName();
    
    @Override
    public Result check(TypeElement template)
    {
        if (!template.getTypeParameters().isEmpty())
        {
            String err = "A capsule template must not have any type parameters, but such a "
                       + "template was found: `{0}`";
            err = format(err, template.getQualifiedName());
            return new Error(err, ERROR_SOURCE);
        }
        return ok;
    }
}
