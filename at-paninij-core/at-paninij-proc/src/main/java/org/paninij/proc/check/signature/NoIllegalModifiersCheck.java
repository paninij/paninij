package org.paninij.proc.check.signature;

import static java.text.MessageFormat.format;

import static javax.lang.model.element.Modifier.DEFAULT;
import static javax.lang.model.element.Modifier.STATIC;

import static org.paninij.proc.check.Result.ok;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import org.paninij.proc.check.Result;
import org.paninij.proc.check.Result.Error;

public class NoIllegalModifiersCheck implements SignatureCheck
{
    public static final String ERROR_SOURCE = NoIllegalModifiersCheck.class.getName();
    
    private static final Modifier[] ILLEGAL_MODIFIERS = {
        DEFAULT,
        STATIC,
    };
    
    private static boolean isIllegalModifier(Modifier m)
    {
        for (Modifier illegal : ILLEGAL_MODIFIERS) {
            if (m == illegal) {
                return true;
            }
        }
        return false;
    }
    
    private static Modifier getIllegalModifierIfAny(ExecutableElement method)
    {
        for (Modifier m : method.getModifiers()) {
            if (isIllegalModifier(m)) {
                return m;
            }
        }
        return null;
    }

    @Override
    public Result checkSignature(TypeElement signature)
    {
        for (Element e : signature.getEnclosedElements()) {
            if (e.getKind() == ElementKind.METHOD) {
                Modifier illegalModifier = getIllegalModifierIfAny((ExecutableElement) e);
                if (illegalModifier != null) {
                    String err = "A signature template, `{0}`, includes a method, `{1}()`, which "
                               + "has an illegal modifier: {2}";
                    err = format(err, signature, e.getSimpleName(), illegalModifier);
                    return new Error(err, ERROR_SOURCE);
                }
            }
        }
        return ok;
    }
}
