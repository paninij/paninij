package org.paninij.proc.check.duckability;

import static java.text.MessageFormat.format;

import static javax.lang.model.element.ElementKind.FIELD;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.STATIC;

import static org.paninij.proc.check.Result.ok;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import org.paninij.proc.check.Result;
import org.paninij.proc.check.Result.Error;

/**
 * Check that all fields of a type to be ducked have valid field modifiers (i.e. check that every
 * field is either `private` or `static`).
 */
public class FieldModifiersCheck implements DuckabilityCheck
{
    private static final String ERROR_SOURCE = FieldModifiersCheck.class.getName();
    
    @Override
    public Result checkDuckability(TypeElement toDuck)
    {
        for (Element elem : toDuck.getEnclosedElements()) {
            if (elem.getKind() == FIELD && ! hasAnyModifier(elem, PRIVATE, STATIC)) {
                String err = "Type `{0}` has a field `{1}` which is not declared either `private` "
                           + "or `static`.";
                err = format(err, toDuck, elem.getSimpleName());
                return new Error(err, ERROR_SOURCE, elem);
            }
        }
        return ok;
    }
    
    /**
     * @return true iff the given `elem` has any of the given modifiers.
     */
    private static boolean hasAnyModifier(Element elem, Modifier... modifiers) {
        for (Modifier m : modifiers) {
            if (elem.getModifiers().contains(m)) {
                return true;
            }
        }
        return false;
    }
}
