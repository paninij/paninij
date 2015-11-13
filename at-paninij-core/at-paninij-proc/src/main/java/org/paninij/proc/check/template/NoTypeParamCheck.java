package org.paninij.proc.check.template;

import static java.text.MessageFormat.format;

import static org.paninij.proc.check.Result.ok;

import javax.lang.model.element.TypeElement;

import org.paninij.proc.check.Result;
import org.paninij.proc.check.Result.Error;


public class NoTypeParamCheck extends AbstractTemplateCheck
{
    @Override
    public Result checkTemplate(TemplateKind templateKind, TypeElement template)
    {
        if (!template.getTypeParameters().isEmpty())
        {
            String err = "A {0} template must not have any type parameters.";
            err = format(err, templateKind);
            return new Error(err, NoTypeParamCheck.class, template);
        }
        return ok;
    }
}
