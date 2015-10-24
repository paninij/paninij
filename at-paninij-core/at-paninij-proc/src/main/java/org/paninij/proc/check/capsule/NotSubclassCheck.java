package org.paninij.proc.check.capsule;

import java.text.MessageFormat;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import org.paninij.proc.check.Result;


/**
 * Checks that that a capsule template is not a subclass of anything except `java.lang.Object`.
 */
public class NotSubclassCheck implements CapsuleCheck
{
    private static final String errorSource = NotSubclassCheck.class.getName();

    private final CapsuleCheckEnvironment env;
    private final TypeMirror expectedSuperclass;
    
    public NotSubclassCheck(CapsuleCheckEnvironment env)
    {
        this.env = env;
        this.expectedSuperclass = env.getElementUtils()
                                     .getTypeElement("java.lang.Object")
                                     .asType();
    }
    
    @Override
    public Result checkCapsule(TypeElement template)
    {
        TypeMirror superclass = template.getSuperclass();
        if (env.getTypeUtils().isSameType(superclass, expectedSuperclass))
        {
            return Result.ok;
        }
        else
        {
            TypeElement elem = (TypeElement) env.getTypeUtils().asElement(superclass);
            String err = "A capsule template must not extend anything except `java.lang.Object`, "
                       + "but `{0}` extends `{1}`.";
            err = MessageFormat.format(err, template.getQualifiedName(), elem.getQualifiedName());
            return new Result.Error(err, errorSource);
        }
    }
}
