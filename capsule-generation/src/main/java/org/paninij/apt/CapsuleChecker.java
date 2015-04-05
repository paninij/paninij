package org.paninij.apt;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

import org.paninij.apt.util.PaniniModelInfo;
import org.paninij.apt.util.Source;


class CapsuleChecker
{
    /**
     * @param template
     * @return `true` if and only if `template` is can be processed as a valid capsule template.
     */
    static boolean check(PaniniPress context, Element template)
    {
        // TODO: give errors when the user annotates an element which cannot be a capsule.
        // TODO: check that the class does not have any inner classes.
        // TODO: check that every interface implemented by a capsule template is a signature.
        // TODO: check that every procedure is not variadic.
        // TODO: check that every procedure returns a class which is NOT final.
        // TODO: check that every procedure returns a non-primitive value (i.e. an object).
        // TODO: check that every passive capsule has one or more procedures.
        // TODO: check that every active capsule has zero procedures.

        if (template.getKind() != ElementKind.CLASS && template.getKind() != ElementKind.INTERFACE)
        {
            context.error("Capsule template must be either a class or an interface.");
            return false;
        }
        
        if (!checkTemplateName(context, template)) {
            return false;
        }

        return true;
    }
    
    private static boolean checkTemplateName(PaniniPress context, Element template)
    {
        String templateName = template.getSimpleName().toString();
        if (! templateName.endsWith(PaniniModelInfo.CAPSULE_TEMPLATE_SUFFIX))
        {
            String msg = Source.lines(0, "Invalid template name: `#0`",
                                         "Every capsule template name must be suffixed with `#1`");
            msg = Source.format(msg, templateName, PaniniModelInfo.CAPSULE_TEMPLATE_SUFFIX);
            context.error(msg);
            return false;
        }
        else if (templateName.length() == PaniniModelInfo.CAPSULE_TEMPLATE_SUFFIX.length())
        {
            String msg = Source.lines(0, "Invalid template name: `#0`",
                                         "Template name can't be the same as the expected suffix");
            msg = Source.format(msg, templateName);
            context.error(msg);
            return false;
        }

        return true;
    }
}
