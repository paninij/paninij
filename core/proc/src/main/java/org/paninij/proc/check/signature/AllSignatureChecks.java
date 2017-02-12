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

package org.paninij.proc.check.signature;

import static org.paninij.proc.check.Check.Result.OK;
import static org.paninij.proc.util.JavaModel.isAnnotatedBy;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

import org.paninij.proc.check.capsule.CheckForRootAnnotation;
import org.paninij.proc.check.core.CheckForIllegalMethodNames;
import org.paninij.proc.check.core.CheckForNestedTypes;
import org.paninij.proc.check.core.CheckForTypeParameters;
import org.paninij.proc.check.core.CheckForIllegalSubtyping;
import org.paninij.proc.check.core.CheckProcAnnotations;
import org.paninij.proc.check.core.CheckForBadAnnotations;
import org.paninij.proc.check.core.CheckPackage;
import org.paninij.proc.check.core.CheckSuffix;


public class AllSignatureChecks implements SignatureCheck
{
    protected final SignatureCheck signatureChecks[];
    protected final ProcessingEnvironment procEnv;

    public AllSignatureChecks(ProcessingEnvironment procEnv)
    {
        this.procEnv = procEnv;
        this.signatureChecks = new SignatureCheck[]
        {
            new CheckSuffix(),
            new CheckPackage(),
            new CheckForNestedTypes(),
            new CheckForRootAnnotation(),
            new CheckForIllegalSubtyping(procEnv),
            new CheckForTypeParameters(),
            new CheckForIllegalModifiers(),
            new CheckForllegalNames(),
            new CheckProcAnnotations(procEnv),
            new CheckForIllegalMethodNames(),
            new CheckForBadAnnotations(),
            new CheckForEventHandlers(),
        };
    }


    /**
     * @param  core  An element to be checked as a signature core.
     * @return An `OK` result if and only if `core` is can be processed as a signature core.
     */
    @Override
    public Result checkSignature(TypeElement core)
    {
        if (! isAnnotatedBy(procEnv, core, "org.paninij.lang.Signature")) {
            String err = "Tried to check an element as a signature core, but it is not "
                       + "annotated with `@Signature`: " + core;
            throw new IllegalArgumentException(err);
        }

        for (org.paninij.proc.check.signature.SignatureCheck check: signatureChecks)
        {
            Result result = check.checkSignature(core);
            if (!result.ok()) {
                return result;
            }
        }

        return OK;
    }
}
