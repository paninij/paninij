package org.paninij.proc.check.signature;

import static java.text.MessageFormat.format;

import static javax.lang.model.element.ElementKind.METHOD;

import static org.paninij.proc.check.Result.ok;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import org.paninij.proc.check.Result;
import org.paninij.proc.check.Result.Error;

public class NoIllegalNamesCheck implements SignatureCheck
{
    private static final String[] ILLEGAL_METHOD_NAMES = {
        "init",
        "design",
        "run",
    };
    
    private static boolean hasIllegalName(ExecutableElement method)
    {
        String name = method.getSimpleName().toString();
        for (String illegalName : ILLEGAL_METHOD_NAMES) {
            if (name.equals(illegalName)) {
                return true;
            }
        }
        return false;
    }
    
    private static Element getIllegalMethodNameIfAny(TypeElement signature)
    {
        for (Element e : signature.getEnclosedElements()) {
            if (e.getKind() == METHOD && hasIllegalName((ExecutableElement) e)) {
                return e;
            }
        }
        return null;
    }
    
    @Override
    public Result checkSignature(TypeElement signature)
    {
        Element illegalMethod = getIllegalMethodNameIfAny(signature);
        if (illegalMethod != null) {
            String err = "A signature template method has an illegal name: `{0}()`";
            err = format(err, illegalMethod.getSimpleName());
            return new Error(err, NoIllegalNamesCheck.class, illegalMethod);
        }
        return ok;
    }
}
