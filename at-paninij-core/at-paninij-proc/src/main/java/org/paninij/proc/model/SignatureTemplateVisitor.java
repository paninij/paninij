
package org.paninij.proc.model;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.SimpleElementVisitor8;

/**
 * The Template visitor as the main visitor for all capsule templates. This class is used by
 * org.paninij.model.Capsule to convert a Capsule Template to an org.paninij.model.ElementCapsule.
 * This class is used when org.paninij.model.ElementCapsule.make(TypeElement e) is called.
 */
public class SignatureTemplateVisitor extends SimpleElementVisitor8<SignatureElement, SignatureElement>
{

    @Override
    public SignatureElement visitType(TypeElement e, SignatureElement signature) {
        signature.setTypeElement(e);
        for (Element enclosed : e.getEnclosedElements()) {
            enclosed.accept(this, signature);
        }
        return signature;
    }

    @Override
    public SignatureElement visitExecutable(ExecutableElement e, SignatureElement signature) {
        signature.addExecutable(e);
        return signature;
    }

}
