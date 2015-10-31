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
    private static final String ERROR_SOURCE = NoIllegalNamesCheck.class.getName();
    
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
    
    private static String getIllegalMethodNameIfAny(TypeElement signature)
    {
        for (Element e : signature.getEnclosedElements()) {
            if (e.getKind() == METHOD && hasIllegalName((ExecutableElement) e)) {
                return e.getSimpleName().toString();
            }
        }
        return null;
    }
    
    @Override
    public Result checkSignature(TypeElement signature)
    {
        String illegalName = getIllegalMethodNameIfAny(signature);
        if (illegalName != null) {
            String err = "Signature template `{0}` includes a method with an illegal name: {1}()";
            err = format(err, signature.getSimpleName(), illegalName);
            return new Error(err, ERROR_SOURCE);
        }
        return ok;
    }
}
