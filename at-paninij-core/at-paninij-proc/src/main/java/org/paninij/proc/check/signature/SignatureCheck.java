package org.paninij.proc.check.signature;

import javax.lang.model.element.TypeElement;

import org.paninij.proc.check.Check;
import org.paninij.proc.check.Result;

public interface SignatureCheck extends Check
{
    /**
     * @param  template  The type element for the signature template to be check.
     * @return The result of the check.
     */
    Result checkSignature(TypeElement template);
}
