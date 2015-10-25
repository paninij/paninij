package org.paninij.proc.check.duckability;

import static java.text.MessageFormat.format;

import static javax.lang.model.element.Modifier.FINAL;

import static org.paninij.proc.check.Result.ok;

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
    
    @Override
    public Result checkDuckability(TypeElement toDuck)
    {
        if (toDuck.getModifiers().contains(FINAL)) {
            String err = format("Type `{0}` is final.", toDuck.getQualifiedName());
            return new Error(err, ERROR_SOURCE);
        }
        
        // TODO: Check methods!
        
        return ok;
    }
}
