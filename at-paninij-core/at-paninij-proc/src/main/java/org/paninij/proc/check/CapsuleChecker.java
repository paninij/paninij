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
package org.paninij.proc.check;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import org.paninij.proc.PaniniProcessor;
import org.paninij.proc.check.capsules.SuffixCheck;


public class CapsuleChecker
{
    protected static TemplateCheck templateChecks[] = {
        new SuffixCheck()
    };

    /**
     * @param template
     * @return `true` if and only if `template` is can be processed as a valid capsule template.
     */
    public static boolean check(PaniniProcessor context, Element template)
    {
        if (template.getKind() != ElementKind.CLASS)
        {
            context.error("Capsule template must be either a class.");
            return false;
        }

        for (TemplateCheck check: templateChecks)
        {
            Result result = check.check((TypeElement) template);
            if (!result.ok())
            {
                context.error(result.err());
                context.error("For more info see: `" + result.source() + "`.");
                return false;
            }
        }

        return true;
    }
}
