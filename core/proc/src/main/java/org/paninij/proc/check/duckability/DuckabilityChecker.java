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
package org.paninij.proc.check.duckability;

import static java.text.MessageFormat.format;

import static org.paninij.proc.check.Check.Result.OK;
import static org.paninij.proc.check.Check.Result.error;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import org.paninij.proc.check.Check;

public class DuckabilityChecker implements Check
{
    private final ProcessingEnvironment procEnv;
    private final DuckabilityCheck[] checks;
    
    public DuckabilityChecker(ProcessingEnvironment procEnv) {
        this.procEnv = procEnv;
        this.checks = new DuckabilityCheck[] {
            new CheckFieldModifiers(),
            new CheckMethodModifiers()
        };
    }
    
    /**
     * @param toDuck  A type element representing some type to be ducked.
     * @return  The result of the duckability check.
     */
    public Result check(TypeMirror toDuck)
    {
        switch (toDuck.getKind()) {
        case VOID:
            return OK;
        case DECLARED:
            return checkDeclared(toDuck);
        default:
            String err = "Cannot duck type `{0}` because it has TypeKind {1}.";
            err = format(err, toDuck, toDuck.getKind());
            return error(err, DuckabilityChecker.class, null);
        }
    }
    
    public Result checkDeclared(TypeMirror toDuck)
    {
        TypeElement toDuckElem = (TypeElement) procEnv.getTypeUtils().asElement(toDuck);
        if (toDuckElem == null) {
            throw new RuntimeException("Could not convert type mirror to type element: " + toDuck);
        }

        for (DuckabilityCheck check : checks) {
            Result result = check.checkDuckability(toDuckElem);
            if (! result.ok()) {
                return result;
            }
        }
        
        return OK;
    }
}
