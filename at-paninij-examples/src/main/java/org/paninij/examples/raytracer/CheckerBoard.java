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
package org.paninij.examples.raytracer;

import java.io.Serializable;

public class CheckerBoard extends Surface implements Serializable {
    private static final long serialVersionUID = 2694218922038717047L;

    @Override
    public Color diffuse(Vector v) {
        return (Math.floor(v.z()) + Math.floor(v.x())) % 2 != 0 ? new Color(1, 1, 1) : new Color(0, 0, 0);
    }

    @Override
    public Color specular(Vector v) {
        return new Color(1, 1, 1);
    }

    @Override
    public Double reflect(Vector v) {
        return (Math.floor(v.z()) + Math.floor(v.x())) % 2 != 0 ? 0.3 : 0.3;
    }

    @Override
    public Double roughness() {
        return 15.0;
    }

}
