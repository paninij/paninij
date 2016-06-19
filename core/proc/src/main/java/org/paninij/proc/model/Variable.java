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

import javax.lang.model.type.TypeMirror;

public class Variable extends Type
{
    private String identifier;
    private boolean isVararg;

    public Variable(TypeMirror mirror, String identifier, boolean isVararg) {
        super(mirror);
        this.identifier = identifier;
        this.isVararg = isVararg;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public String toString() {
        String type = super.toString();
        if (isVararg) {
            type = type.replace("[]", "...");
        }
        return type + " " + this.identifier;
    }
}