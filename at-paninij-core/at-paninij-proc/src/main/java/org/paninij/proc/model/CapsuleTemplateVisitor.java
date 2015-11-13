
package org.paninij.proc.model;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.SimpleElementVisitor8;

import org.paninij.lang.Local;
import org.paninij.lang.Imports;

/**
 * The Template visitor as the main visitor for all capsule templates. This class is used by
 * org.paninij.model.Capsule to convert a Capsule Template to an org.paninij.model.ElementCapsule.
 * This class is used when org.paninij.model.ElementCapsule.make(TypeElement e) is called.
 */
public class CapsuleTemplateVisitor extends SimpleElementVisitor8<CapsuleElement, CapsuleElement>
{
    @Override
    public CapsuleElement visitType(TypeElement e, CapsuleElement capsule) {
        capsule.setTypeElement(e);
        for (Element enclosed : e.getEnclosedElements()) {
            enclosed.accept(this, capsule);
        }
        return capsule;
    }

    @Override
    public CapsuleElement visitExecutable(ExecutableElement e, CapsuleElement capsule) {
        capsule.addExecutable(e);
        return capsule;
    }

    @Override
    public CapsuleElement visitVariable(VariableElement e, CapsuleElement capsule) {
        Variable variable = new Variable(e.asType(), e.getSimpleName().toString());
        if (e.getAnnotation(Local.class) != null) {
            capsule.addLocals(variable);
        } else if (e.getAnnotation(Imports.class) != null) {
            capsule.addImportDecl(variable);
        } else {
            capsule.addState(variable);
        }
        return capsule;
    }
}
