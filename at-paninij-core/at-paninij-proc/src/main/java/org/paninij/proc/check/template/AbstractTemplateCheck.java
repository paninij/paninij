package org.paninij.proc.check.template;

import javax.lang.model.element.TypeElement;

import org.paninij.proc.check.Result;
import org.paninij.proc.check.capsule.CapsuleCheck;
import org.paninij.proc.check.signature.SignatureCheck;

public abstract class AbstractTemplateCheck implements CapsuleCheck, SignatureCheck
{
    public enum TemplateKind
    {
        CAPSULE,
        SIGNATURE;
        
        public String toString() {
            switch (this) {
            case CAPSULE:
                return "capsule";
            case SIGNATURE:
                return "signature";
            default:
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public Result checkCapsule(TypeElement template) {
        return checkTemplate(TemplateKind.CAPSULE, template);
    }

    @Override
    public Result checkSignature(TypeElement template) {
        return checkTemplate(TemplateKind.SIGNATURE, template);
    }
    
    protected abstract Result checkTemplate(TemplateKind templateKind, TypeElement template);
}
