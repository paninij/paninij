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
package org.paninij.proc.shapes;

import org.paninij.lang.Capsule;


@Capsule
public class NormalTemplate
{
    public Object foo(Object o, Object i) {
        return new Object();
    }

    public Object primitiveArg(int i)
    {
        return new Object();
    }

    public Object tooManyArgs(boolean a, boolean b, boolean c, boolean d, boolean e, boolean f,
                              boolean g, boolean h, boolean i, boolean j, boolean k, boolean l,
                              boolean m, boolean n, boolean o, boolean p, boolean q, boolean r,
                              boolean s, boolean t, boolean u, boolean v, boolean w, boolean x,
                              boolean y, boolean z)
    {
        return new Object();
    }

    public Object arrayArg(Object[] arr) {
        return new Object();
    }

    public Object primitiveArrayArg(int[] arr) {
        return new Object();
    }
}