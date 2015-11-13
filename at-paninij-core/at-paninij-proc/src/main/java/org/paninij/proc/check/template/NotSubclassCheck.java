package org.paninij.proc.check.template;

import static org.paninij.proc.check.Result.ok;

import java.util.List;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import org.paninij.proc.check.CheckEnvironment;
import org.paninij.proc.check.Result;
import org.paninij.proc.check.Result.Error;
import org.paninij.proc.check.capsule.CapsuleCheck;
import org.paninij.proc.check.signature.SignatureCheck;


/**
 * Checks that that a capsule template or signature template is not a subclass of anything except
 * `java.lang.Object`.
 */
public class NotSubclassCheck implements CapsuleCheck, SignatureCheck
{
    private final CheckEnvironment env;
    private final TypeMirror javaLangObject;
    
    public NotSubclassCheck(CheckEnvironment env)
    {
        this.env = env;
        this.javaLangObject = env.getElementUtils().getTypeElement("java.lang.Object").asType();
    }

    @Override
    public Result checkSignature(TypeElement template)
    {
        List<? extends TypeMirror> interfaces = template.getInterfaces();
        if (!interfaces.isEmpty()) {
            String err = "A signature template must not be a subinterface.";
            return new Error(err, NotSubclassCheck.class, template);
        }
        return ok;
    }

    @Override
    public Result checkCapsule(TypeElement template)
    {
        TypeMirror superclass = template.getSuperclass();
        if (! env.getTypeUtils().isSameType(superclass, javaLangObject))
        {
            String err = "A capsule template must not extend anything except `java.lang.Object`.";
            return new Error(err, NotSubclassCheck.class, template);
        }

        return ok;
    }
}
