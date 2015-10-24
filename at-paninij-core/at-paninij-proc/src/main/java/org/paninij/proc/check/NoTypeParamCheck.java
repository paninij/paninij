package org.paninij.proc.check;

import static java.text.MessageFormat.format;

import static org.paninij.proc.check.Result.ok;

import javax.lang.model.element.TypeElement;

import org.paninij.proc.check.Result.Error;


public class NoTypeParamCheck extends AbstractTemplateCheck
{
    public final static String ERROR_SOURCE = NoTypeParamCheck.class.getName();
    
    @Override
    public Result checkTemplate(String templateType, TypeElement template)
    {
        if (!template.getTypeParameters().isEmpty())
        {
            String err = "{0} templates must not have any type parameters, but such a "
                       + "template was found: `{1}`";
            err = format(err, templateType, template.getQualifiedName());
            return new Error(err, ERROR_SOURCE);
        }
        return ok;
    }
}
