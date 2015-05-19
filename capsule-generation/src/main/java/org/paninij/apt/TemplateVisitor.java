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

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.SimpleElementVisitor8;

import org.paninij.model.ElementCapsule;

/**
 * The Template visitor as the main visitor for all capsule templates. This class effectively
 *  converts a Capsule Template to an org.paninij.model.Capsule.
 *
 */
public class TemplateVisitor extends SimpleElementVisitor8<ElementCapsule, ElementCapsule>
{

    public TemplateVisitor() {
    }

    @Override
    public ElementCapsule visitType(TypeElement e, ElementCapsule capsule) {
        capsule.setTypeElement(e);
        return capsule;
    }

    @Override
    public ElementCapsule visitExecutable(ExecutableElement e, ElementCapsule capsule) {
        capsule.addExecutable(e);
        return capsule;
    }

}
