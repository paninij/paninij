/*******************************************************************************
 * This file is part of the Panini project at Iowa State University.
 *
 * @PaniniJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * @PaniniJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with @PaniniJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributors:
 * 	Dr. Hridesh Rajan,
 * 	Dalton Mills,
 * 	David Johnston,
 * 	Trey Erenberger
 *******************************************************************************/

package org.paninij.proc.model;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.SimpleElementVisitor8;

/**
 * The Core visitor as the main visitor for all capsule cores. This class is used by
 * org.paninij.model.Capsule to convert a Capsule Core to an org.paninij.model.ElementCapsule.
 * This class is used when org.paninij.model.ElementCapsule.make(TypeElement e) is called.
 */
public class SignatureCoreVisitor extends SimpleElementVisitor8<SignatureElement, SignatureElement>
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
