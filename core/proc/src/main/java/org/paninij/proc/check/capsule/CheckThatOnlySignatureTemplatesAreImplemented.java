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
package org.paninij.proc.check.capsule;

import static java.text.MessageFormat.format;
import static javax.lang.model.type.TypeKind.DECLARED;
import static org.paninij.proc.check.Check.Result.OK;
import static org.paninij.proc.check.Check.Result.error;
import static org.paninij.proc.util.JavaModel.isAnnotatedBy;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;


/**
 * Check that a capsule template only implements signature templates.
 */
public class CheckThatOnlySignatureTemplatesAreImplemented implements CapsuleCheck
{
    private final ProcessingEnvironment procEnv;
    
    public CheckThatOnlySignatureTemplatesAreImplemented(ProcessingEnvironment procEnv) {
        this.procEnv = procEnv;
    }
    
    @Override
    public Result checkCapsule(TypeElement template)
    {
        for (TypeMirror type : template.getInterfaces())
        {
            if (! isSignatureTemplateType(type)) {
                String err = format("A capsule template may only implement signature templates, "
                                  + "but this template implements `{0}`.", type.toString());
                return error(err, CheckThatOnlySignatureTemplatesAreImplemented.class, template);
            }
        }
        return OK;
    }
    
    private boolean isSignatureTemplateType(TypeMirror tm) {
        return tm.getKind() == DECLARED && isAnnotatedBy(procEnv, tm, "org.paninij.lang.Signature");
    }
}
