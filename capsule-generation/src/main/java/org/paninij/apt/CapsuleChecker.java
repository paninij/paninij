package org.paninij.apt;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;


class CapsuleChecker {
    /**
     * @param template
     * @return `true` if and only if `elem` is can be processed as a valid
     * capsule.
     */
    static boolean check(PaniniPress context, Element template)
    {
        // TODO: give errors when the user annotates an element which cannot be a capsule.
        // TODO: check that the class does not have any inner classes.
        return template.getKind() == ElementKind.CLASS;
    }
}
