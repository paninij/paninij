package org.paninij.proc.check.signature;

import static org.paninij.proc.check.Result.ok;

import javax.lang.model.element.TypeElement;

import org.paninij.lang.Root;
import org.paninij.proc.check.Result;
import org.paninij.proc.check.Result.Error;

public class NoRootAnnotationCheck implements SignatureCheck
{
    @Override
    public Result checkSignature(TypeElement template) {
        if (template.getAnnotation(Root.class) == null) {
            return ok;
        } else {
            String err = "A signature template must not be annotated with `@Root`.";
            return new Error(err, NoRootAnnotationCheck.class, template);
        }
    }
}
