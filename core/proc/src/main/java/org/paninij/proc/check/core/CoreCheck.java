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
package org.paninij.proc.check.core;

import javax.lang.model.element.TypeElement;

import org.paninij.proc.check.capsule.CapsuleCheck;
import org.paninij.proc.check.signature.SignatureCheck;

import static org.paninij.proc.check.core.CoreKind.CAPSULE;
import static org.paninij.proc.check.core.CoreKind.SIGNATURE;

/**
 * An interface for a check which can act as both a {@link CapsuleCheck} and a {@link
 * SignatureCheck}. By default, calls to {@link #checkCapsule} and {@link #checkSignature} are
 * passed on to a single implementer-defined {@link #checkCore} method (without any
 * validation).
 */
public interface CoreCheck extends CapsuleCheck, SignatureCheck
{
    default Result checkCapsule(TypeElement core) {
        return checkCore(core, CAPSULE);
    }

    default Result checkSignature(TypeElement core) {
        return checkCore(core, SIGNATURE);
    }

    Result checkCore(TypeElement core, CoreKind kind);
}
