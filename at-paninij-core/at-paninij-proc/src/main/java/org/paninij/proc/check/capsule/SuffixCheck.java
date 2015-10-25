package org.paninij.proc.check.capsule;

import javax.lang.model.element.TypeElement;

import org.paninij.proc.check.Result;
import org.paninij.proc.util.PaniniModel;
import org.paninij.proc.util.Source;


/**
 * Checks that a capsule template is named with the appropriate suffix.
 */
public class SuffixCheck implements CapsuleCheck
{
    public static String errorSource = SuffixCheck.class.getName();

    @Override
    public Result checkCapsule(TypeElement template)
    {
        String templateName = template.getSimpleName().toString();
        String err = null;
        if (! templateName.endsWith(PaniniModel.CAPSULE_TEMPLATE_SUFFIX))
        {
            err = Source.cat("Invalid template name: `#0`",
                             "Every capsule template name must be suffixed with `#1`");
            err = Source.format(err, templateName, PaniniModel.CAPSULE_TEMPLATE_SUFFIX);
        }
        else if (templateName.length() == PaniniModel.CAPSULE_TEMPLATE_SUFFIX.length())
        {
            err = Source.cat("Invalid template name: `#0`",
                             "Template name can't be the same as the expected suffix");
            err = Source.format(err, templateName);
        }

        return (err == null) ? Result.ok : new Result.Error(err, errorSource);
    }
}
