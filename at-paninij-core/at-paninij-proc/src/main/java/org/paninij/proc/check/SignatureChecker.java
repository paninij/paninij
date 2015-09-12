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

import org.paninij.lang.Signature;
import org.paninij.proc.PaniniProcessor;
import org.paninij.proc.util.Source;

public class SignatureChecker {
    /**
     * @param template
     * @return `true` if and only if `elem` is can be processed as a valid
     * capsule.
     */
    public static boolean check(PaniniProcessor context, Element template)
    {
        if (template.getAnnotation(Signature.class) == null)
        {
            String msg = "Tried to run signature checks on #0, but that element doesn't have `@Signature`.";
            throw new IllegalArgumentException(Source.format(msg, template));
        }
        
        // TODO: check that the interface does not include defaults

        if (template.getKind() != ElementKind.INTERFACE)
        {
            String msg = Source.format("`#0` has `@Signature`, but it's `#1`, not an interface.",
                                       template, template.getKind());
            context.error(msg);
            return false;
        }
        return true;
    }
}
