/*******************************************************************************
 * This file is part of the Panini project at Iowa State University.
 *
 * @PaniniJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * @PaniniJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with @PaniniJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributors:
 * 	Dr. Hridesh Rajan,
 * 	Dalton Mills,
 * 	David Johnston,
 * 	Trey Erenberger
 *******************************************************************************/
package org.paninij.proc.check.duckability;

import static java.text.MessageFormat.format;

import static javax.lang.model.element.ElementKind.METHOD;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PROTECTED;
import static javax.lang.model.element.Modifier.PUBLIC;

import static org.paninij.proc.check.Check.Result.OK;
import static org.paninij.proc.check.Check.Result.error;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * Checks to see whether a type's methods have valid modifiers.
 */
public class CheckMethodModifiers implements DuckabilityCheck
{
    // Currently, these ignored names are the `final` methods in `java.lang.Object`.
    private static final String[] IGNORED_METHOD_NAMES = {
        "getClass",
        "notify",
        "notifyAll",
        "wait",
    };
    
    private static final String[] PROTECTED_PACKAGE_PREFIXES = {
        "java.",
        "javax.",
    };
    
    @Override
    public Result checkDuckability(TypeElement toDuck)
    {
        Result result = null;

        if (toDuck.getModifiers().contains(FINAL)) {
            String err = format("Type `{0}` is final.", toDuck.getQualifiedName());
            return error(err, CheckMethodModifiers.class, toDuck);
        }
        
        result = checkForIllegalFinalMethod(toDuck);
        if (!result.ok()) {
            return result;
        }
        
        result = checkForProblematicProtectedMethod(toDuck);
        if (!result.ok()) {
            return result;
        }
        
        return OK;
    }

    /**
     * Check to see if the given type element to be ducked has any final methods which are
     * non-private. Currently, such methods cause a type to be unduckable because all visible
     * methods need a facade, but `final` methods cannot be overridden. Therefore a facade cannot be
     * made for all methods of the type.
     */
    public Result checkForIllegalFinalMethod(TypeElement toDuck)
    {
        for (Element elem : toDuck.getEnclosedElements()) {
            if (isIllegalFinalMethod(elem)) {
                String err = "Type `{0}` has a non-private final method: `{1}()`.";
                err = format(err, toDuck.getQualifiedName(), elem.getSimpleName());
                return error(err, CheckMethodModifiers.class, elem);
            }
        }
        return OK;
    }
    
    private static boolean isIllegalFinalMethod(Element elem)
    {
        if (elem.getKind() != ElementKind.METHOD || ! hasModifier(elem, FINAL)) {
            return false;
        }
        if (hasModifier(elem, PRIVATE)) {
            return false;
        }
        // TODO: Make this check more precise!
        if (hasNameOfIgnoredMethod(elem)) {
            return false;
        }
        
        return true;
    } 

    /**
     * Checks to see whether a type is both in a protected package and has a package private method.
     * Currently, these cases cannot be correctly ducked, since the duck wrapper cannot be put in
     * these packages and a duck wrapper placed in some other package cannot call a package
     * private method.
     */
    public Result checkForProblematicProtectedMethod(TypeElement toDuck)
    {
        if (isInProtectedPackage(toDuck) && hasPackagePrivateMethod(toDuck)) {
            String err = "Cannot duck type `{0}` because it is both in a protected package and has "
                       + "a method with package-private visibility (a.k.a. default visibility).";
            err = format(err, toDuck.getQualifiedName());
            return error(err, CheckMethodModifiers.class, toDuck);
        }
        return OK;
    }
    
    private static boolean isInProtectedPackage(TypeElement toDuck)
    {
        String name = toDuck.getQualifiedName().toString();
        for (String prefix : PROTECTED_PACKAGE_PREFIXES) {
            if (name.startsWith(prefix)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * @return `true` iff the given type element has a method with package-private visibility
     *         (a.k.a. default visibility). Package-private visibility is usually indicated when an
     *         element is neither `public`, `protected`, nor `private`.
     */
    private static boolean hasPackagePrivateMethod(TypeElement elem)
    {
        for (Element member : elem.getEnclosedElements()) {
            if (member.getKind() == METHOD && isPackagePrivate(member)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean isPackagePrivate(Element elem) {
        return !hasAnyModifier(elem, PUBLIC, PROTECTED, PRIVATE);
    }
    
    private static boolean hasAnyModifier(Element elem, Modifier... modifiers)
    {
        for (Modifier m : modifiers) {
            if (hasModifier(elem, m)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean hasModifier(Element elem, Modifier m) {
        return elem.getModifiers().contains(m);
    }
    
    private static boolean hasNameOfIgnoredMethod(Element elem)
    {
        String name = elem.getSimpleName().toString();
        for (String ignored : IGNORED_METHOD_NAMES) {
            if (name.equals(ignored)) {
                return true;
            }
        }
        return false;
    }
}
