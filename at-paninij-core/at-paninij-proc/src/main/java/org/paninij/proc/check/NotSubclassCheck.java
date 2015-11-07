package org.paninij.proc.check;

import static java.text.MessageFormat.format;

import static org.paninij.proc.check.Result.ok;
import static org.paninij.proc.check.Result.Error;

import java.util.List;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import org.paninij.proc.check.capsule.CapsuleCheck;
import org.paninij.proc.check.signature.SignatureCheck;


/**
 * Checks that that a capsule template or signature template is not a subclass of anything except
 * `java.lang.Object`.
 */
public class NotSubclassCheck implements CapsuleCheck, SignatureCheck
{
    private static final String ERROR_SOURCE = NotSubclassCheck.class.getName();

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
            String err = "Signature templates must not be subinterfaces, but `{0}` extends `{1}`.";
            err = format(err, template.getQualifiedName(), interfaces.get(0));
            return new Error(err, ERROR_SOURCE, template);
        }
        return ok;
    }

    @Override
    public Result checkCapsule(TypeElement template)
    {
        TypeMirror superclass = template.getSuperclass();
        if (! env.getTypeUtils().isSameType(superclass, javaLangObject))
        {
            TypeElement elem = (TypeElement) env.getTypeUtils().asElement(superclass);
            String err = "Capsule templates must not extend anything except `java.lang.Object`, "
                       + "but `{0}` extends `{1}`.";
            err = format(err, template.getQualifiedName(), elem.getQualifiedName());
            return new Error(err, ERROR_SOURCE, template);
        }

        return ok;
    }
}
