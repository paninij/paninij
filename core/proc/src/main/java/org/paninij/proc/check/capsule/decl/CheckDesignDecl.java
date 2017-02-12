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
package org.paninij.proc.check.capsule.decl;

import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import org.paninij.proc.check.Check;

public class CheckDesignDecl extends DeclCheck
{
    @Override
    public Class<? extends Check> getErrorSource() {
        return CheckDesignDecl.class;
    }

    @Override
    public String getDeclName() {
        return "design";
    }

    @Override
    public boolean hasValidParameters(TypeElement core, ExecutableElement decl)
    {
        List<? extends VariableElement> params = decl.getParameters();
        if (params.size() != 1) {
            return false;
        }

        // Note that `self` is usually `NONE` type kind, which indicates that a capsule interface
        // artifact has yet not been generated. However, if there is already a capsule interface
        // type around, then we will have fully qualified type information. This is why we compare
        // the `actual` with the suffix of the `expected`.
        TypeMirror self = params.get(0).asType();
        String actual = self.toString() + "Core";
        String expected = core.getQualifiedName().toString();

        return expected.endsWith(actual);
    }

}
