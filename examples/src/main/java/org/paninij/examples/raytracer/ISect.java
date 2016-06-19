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

public class ISect implements Serializable {
    private static final long serialVersionUID = 6601281940824842142L;
    private SceneObject thing;
    private Ray ray;
    private double dist;

    public ISect(SceneObject thing, Ray ray, double dist) {
        this.thing = thing;
        this.ray = ray;
        this.dist = dist;
    }

    public SceneObject thing() {
        return thing;
    }

    public Ray ray() {
        return ray;
    }

    public double dist() {
        return dist;
    }
}
