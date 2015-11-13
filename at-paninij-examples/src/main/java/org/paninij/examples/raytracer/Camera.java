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

public class Camera implements Serializable {
    private static final long serialVersionUID = 4285425187179729621L;
    private Vector position;
    private Vector lookAt;

    public Camera(Vector position, Vector lookAt) {
        this.position = position;
        this.lookAt = lookAt;
    }

    public Camera() {
        this.position = new Vector(0, 0, 0);
        this.lookAt = new Vector(0, 0, 0);
    }

    public Vector position() {
        return position;
    }

    public Vector down() {
        return new Vector(0, -1, 0);
    }

    public Vector forward() {
        return lookAt.sub(position).norm();
    }

    public Vector right() {
        return forward().cross(down()).norm().mult(1.5);
    }

    public Vector up() {
        return forward().cross(right()).norm().mult(1.5);
    }
}
