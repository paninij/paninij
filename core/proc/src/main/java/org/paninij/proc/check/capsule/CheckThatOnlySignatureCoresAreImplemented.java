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
 * Check that a capsule core only implements signature cores.
 */
public class CheckThatOnlySignatureCoresAreImplemented implements CapsuleCheck
{
    private final ProcessingEnvironment procEnv;
    
    public CheckThatOnlySignatureCoresAreImplemented(ProcessingEnvironment procEnv) {
        this.procEnv = procEnv;
    }
    
    @Override
    public Result checkCapsule(TypeElement core)
    {
        for (TypeMirror type : core.getInterfaces())
        {
            if (! isSignatureCoreType(type)) {
                String err = format("A capsule core may only implement signature cores, "
                                  + "but this core implements `{0}`.", type.toString());
                return error(err, CheckThatOnlySignatureCoresAreImplemented.class, core);
            }
        }
        return OK;
    }
    
    private boolean isSignatureCoreType(TypeMirror tm) {
        return tm.getKind() == DECLARED && isAnnotatedBy(procEnv, tm, "org.paninij.lang.Signature");
    }
}
