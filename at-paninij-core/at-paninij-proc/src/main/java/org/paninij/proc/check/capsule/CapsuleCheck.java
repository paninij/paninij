package org.paninij.proc.check.capsule;

import javax.lang.model.element.TypeElement;

import org.paninij.proc.check.Check;
import org.paninij.proc.check.Result;

public interface CapsuleCheck extends Check
{
    /**
     * @param   template  A type element for the capsule template to be checked.
     * @return  The result of the check.
     */
    Result checkCapsule(TypeElement template);
}