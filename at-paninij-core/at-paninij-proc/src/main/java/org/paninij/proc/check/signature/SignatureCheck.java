package org.paninij.proc.check.signature;

import javax.lang.model.element.TypeElement;

import org.paninij.proc.check.Result;

public interface SignatureCheck
{
    Result checkSignature(TypeElement signature);
}
