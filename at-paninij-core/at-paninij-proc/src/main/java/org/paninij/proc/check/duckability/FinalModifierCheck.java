package org.paninij.proc.check.duckability;

import static java.text.MessageFormat.format;

import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.NATIVE;
import static javax.lang.model.element.Modifier.PRIVATE;

import static org.paninij.proc.check.Result.ok;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import org.paninij.proc.check.Result;
import org.paninij.proc.check.Result.Error;

/**
 * Checks to see whether a type is final or if one of its (non-private, non-static) methods is
 * marked as `final`.
 */
public class FinalModifierCheck implements DuckabilityCheck
{
    private static final String ERROR_SOURCE = FinalModifierCheck.class.getName();
    
    // Currently, these ignored are the `final` methods in `java.lang.Object`.
    private static final String[] IGNORED_METHOD_NAMES = {
        "getClass",
        "notify",
        "notifyAll",
        "wait",
    };
    
    @Override
    public Result checkDuckability(TypeElement toDuck)
    {
        if (toDuck.getModifiers().contains(FINAL)) {
            String err = format("Type `{0}` is final.", toDuck.getQualifiedName());
            return new Error(err, ERROR_SOURCE);
        }
        
        for (Element elem : toDuck.getEnclosedElements()) {
            if (isIllegalFinalMethod(elem)) {
                String err = "Type `{0}` has a non-private final method: `{1}`().";
                err = format(err, toDuck.getQualifiedName(), elem.getSimpleName());
                return new Error(err, ERROR_SOURCE);
            }
        }
        
        return ok;
    }
    
    private static boolean isIllegalFinalMethod(Element elem)
    {
        if (elem.getKind() != ElementKind.METHOD || ! hasModifier(elem, FINAL)) {
            return false;
        }
        if (hasModifier(elem, PRIVATE) || hasModifier(elem, NATIVE)) {
            return false;
        }
        if (hasNameOfIgnoredMethod(elem)) {
            return false;
        }
        
        return true;
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
