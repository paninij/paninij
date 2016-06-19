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
package org.paninij.proc.check.template;

import javax.lang.model.element.TypeElement;

import org.paninij.proc.check.Result;
import org.paninij.proc.check.capsule.CapsuleCheck;
import org.paninij.proc.check.signature.SignatureCheck;

public abstract class AbstractTemplateCheck implements CapsuleCheck, SignatureCheck
{
    public enum TemplateKind
    {
        CAPSULE,
        SIGNATURE;
        
        public String toString() {
            switch (this) {
            case CAPSULE:
                return "capsule";
            case SIGNATURE:
                return "signature";
            default:
                throw new IllegalStateException();
            }
        }
    }
    
    @Override
    public Result checkCapsule(TypeElement template) {
        return checkTemplate(TemplateKind.CAPSULE, template);
    }

    @Override
    public Result checkSignature(TypeElement template) {
        return checkTemplate(TemplateKind.SIGNATURE, template);
    }
    
    protected abstract Result checkTemplate(TemplateKind templateKind, TypeElement template);
}
