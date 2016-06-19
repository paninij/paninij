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

public class Sphere extends SceneObject implements Serializable {
    private static final long serialVersionUID = 9203957170366335623L;
    private Surface sface;
    private Vector center;
    private double radius;

    public Sphere(Surface sface, Vector center, double radius) {
        this.sface = sface;
        this.center = center;
        this.radius = radius;
    }

    @Override
    public ISect intersect(Ray ray) {
        Vector eo = center.sub(ray.start());
        Double v = eo.dot(ray.dir());
        Double dist = 0.0;
        if (v < 0) {
            dist = 0.0;
        } else {
            double disc = Math.pow(radius, 2) - (eo.dot(eo) - Math.pow(v,  2));
            if (disc < 0) {
                dist = 0.0;
            } else {
                dist = v - Math.sqrt(disc);
            }
        }
        return dist == 0 || dist.equals(Double.NaN) ? null : new ISect(this, ray, dist);
    }

    @Override
    public Vector normal(Vector pos) {
        return pos.sub(center).norm();
    }

    @Override
    public Surface sface() {
        return sface;
    }

}
