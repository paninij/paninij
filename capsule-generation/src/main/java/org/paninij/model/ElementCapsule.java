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
package org.paninij.model;


import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import org.paninij.apt.TemplateVisitor;
import org.paninij.apt.util.PaniniModelInfo;

public class ElementCapsule extends Capsule
{
    private TypeElement element;

    /*
     * Generate a Capsule from a TypeElement. The TypeElement should already be checked for
     * any errors. The TypeElement should be annotated with @Capsule and should represent
     * a CapsuleTemplate
     */
    public static Capsule make(TypeElement e) {
        ElementCapsule capsule = new ElementCapsule();
        TemplateVisitor visitor = new TemplateVisitor();
        e.accept(visitor, capsule);
        return capsule;
    }

    public ElementCapsule() {
        super();
    }

    public void addExecutable(ExecutableElement e) {
        if (PaniniModelInfo.isProcedure(e)) {
            this.procedures.add(new ElementProcedure(e));
        }
    }

    public void setTypeElement(TypeElement e) {
        if (this.element == null) {
            this.element = e;
            // TODO drop "Template"
            this.simpleName = e.getSimpleName().toString();
            this.qualifiedName = e.getQualifiedName().toString();
        }
    }
}
