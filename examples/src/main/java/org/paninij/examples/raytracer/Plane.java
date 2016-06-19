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

public class Plane extends SceneObject implements Serializable {
    private static final long serialVersionUID = 1154526550077527142L;
    private Surface sface;
    private Vector n;
    private double offset;

    public Plane(Surface sface, Vector n, double offset) {
        this.sface = sface;
        this.n = n;
        this.offset = offset;
    }

    @Override
    public ISect intersect(Ray ray) {
        Double denom = n.dot(ray.dir());
        if (denom > 0 || denom.equals(Double.NaN)) {
            return null;
        }
        Double dist = (n.dot(ray.start()) + offset) / (-denom);
        return dist.equals(Double.NaN) ? null : new ISect(this, ray, dist);
    }

    @Override
    public Vector normal(Vector pos) {
        return n;
    }

    @Override
    public Surface sface() {
        return sface;
    }

}
