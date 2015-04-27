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
