/*
 * This file is part of the Panini project at Iowa State University.
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributor(s): Dalton Mills
 */
package org.paninij.apt;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.SimpleElementVisitor8;

import org.paninij.lang.Child;
import org.paninij.lang.Wired;
import org.paninij.model.CapsuleElement;
import org.paninij.model.Variable;

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
        if (e.getAnnotation(Child.class) != null) {
            capsule.addChild(new Variable(e.asType(), e.getSimpleName().toString()));
        } else if (e.getAnnotation(Wired.class) != null) {
            capsule.addWired(new Variable(e.asType(), e.getSimpleName().toString()));
        }
        return capsule;
    }

}
