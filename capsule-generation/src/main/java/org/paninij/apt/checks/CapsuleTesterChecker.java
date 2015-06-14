package org.paninij.apt.checks;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

import org.paninij.apt.PaniniProcessor;
import org.paninij.apt.util.PaniniModelInfo;

public class CapsuleTesterChecker
{
    /*
     * TODO: Check that a user-defined capsule tester template is well formed. Some criteria:
     * 
     *  - A tester's name is suffixed with `CAPSULE_TESTER_SUFFIX`.
     *  - A tester must be a class annotated with `@CapsuleTester`.
     *  - A tester has no procedures, only tests. Private methods are allowed.
     *  - A tester does not have a run declaration. (All @Test methods are essentially run decls.)
     *  - All tests take no arguments and return void.
     */
    public static boolean check(PaniniProcessor context, Element tester)
    {
        return (checkElementKind(context, tester)
             && checkName(context, tester));
    }
    
    private static boolean checkElementKind(PaniniProcessor context, Element tester)
    {
        return tester.getKind() == ElementKind.CLASS;
    }
    
    private static boolean checkName(PaniniProcessor context, Element tester)
    {
        // TODO: Add helpful error messages.

        String name = tester.getSimpleName().toString();
        return (name.length() > PaniniModelInfo.CAPSULE_TESTER_SUFFIX.length()
             && name.endsWith(PaniniModelInfo.CAPSULE_TESTER_SUFFIX));
    }
}
