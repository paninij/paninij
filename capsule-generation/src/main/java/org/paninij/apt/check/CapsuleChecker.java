/*
 * This file is part of the Panini project at Iowa State University.
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributor(s): Dalton Mills, David Johnston, Trey Erenberger
 */
package org.paninij.apt.check;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

import org.paninij.apt.PaniniProcessor;
import org.paninij.apt.util.PaniniModelInfo;
import org.paninij.apt.util.Source;


public class CapsuleChecker
{
    /**
     * @param template
     * @return `true` if and only if `template` is can be processed as a valid capsule template.
     */
    public static boolean check(PaniniProcessor context, Element template)
    {
        // TODO: give errors when the user annotates an element which cannot be a capsule.
        // TODO: check that the class does not have any inner classes.
        // TODO: check that every interface implemented by a capsule template is a signature.
        // TODO: check that every procedure is not variadic.
        // TODO: check that every procedure returns a class which is NOT final.
        // TODO: check that every procedure returns a non-primitive value (i.e. an object).
        // TODO: check that every passive capsule has one or more procedures.
        // TODO: check that every active capsule has zero procedures.

        if (template.getKind() == ElementKind.FIELD)
        {
            // Ignore type checking if the given element is actually a field.
            return false;
        }

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

    private static boolean checkTemplateName(PaniniProcessor context, Element template)
    {
        String templateName = template.getSimpleName().toString();
        if (! templateName.endsWith(PaniniModelInfo.CAPSULE_TEMPLATE_SUFFIX))
        {
            String msg = Source.cat("Invalid template name: `#0`",
                                    "Every capsule template name must be suffixed with `#1`");
            msg = Source.format(msg, templateName, PaniniModelInfo.CAPSULE_TEMPLATE_SUFFIX);
            context.error(msg);
            return false;
        }
        else if (templateName.length() == PaniniModelInfo.CAPSULE_TEMPLATE_SUFFIX.length())
        {
            String msg = Source.cat("Invalid template name: `#0`",
                                    "Template name can't be the same as the expected suffix");
            msg = Source.format(msg, templateName);
            context.error(msg);
            return false;
        }

        return true;
    }
}
