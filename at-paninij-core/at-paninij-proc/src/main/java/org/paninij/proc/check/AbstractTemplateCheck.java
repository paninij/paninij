package org.paninij.proc.check;

import javax.lang.model.element.TypeElement;

import org.paninij.proc.check.capsule.CapsuleCheck;
import org.paninij.proc.check.signature.SignatureCheck;

public abstract class AbstractTemplateCheck implements CapsuleCheck, SignatureCheck
{
    @Override
    public Result checkCapsule(TypeElement template) {
        return checkTemplate("Capsule", template);
    }

    @Override
    public Result checkSignature(TypeElement template) {
        return checkTemplate("Signature", template);
    }
    
    protected abstract Result checkTemplate(String templateType, TypeElement template);
}
