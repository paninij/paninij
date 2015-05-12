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
package org.paninij.apt;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

public class SignatureChecker {
    /**
     * @param template
     * @return `true` if and only if `elem` is can be processed as a valid
     * capsule.
     */
    static boolean check(PaniniProcessor context, Element template)
    {
        // TODO: check that the interface does not include defaults

        if (template.getKind() == ElementKind.FIELD)
        {
            // Ignore any fields annotated with `@Signature`.
            return false;
        }

        if (template.getKind() != ElementKind.INTERFACE) {
            context.error("\"" + template.getKind().toString() + "\" @Signature cannot be a Signature because it is not an interface.");
            return false;
        }
        return true;
    }
}
