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

public class Vector implements Serializable {
    private static final long serialVersionUID = 7518308319579154444L;
    private double[] v;

    public Vector(double x, double y, double z) {
        this.v = new double[] {x, y, z};
    }

    public Vector(double[] v) {
        this.v = v;
    }

    public Vector(Vector v) {
        this.v = new double[] { v.x(), v.y(), v.z() };
    }

    public double x() {
        return v[0];
    }

    public double y() {
        return v[1];
    }

    public double z() {
        return v[2];
    }

    public Vector mult(double o) {
        return new Vector(x() * o, y() * o, z() * o);
    }

    public Vector mult(Vector o) {
        return new Vector(x() * o.x(), y() * o.y(), z() * o.z());
    }

    public Vector sub(Vector o) {
        return new Vector(x() - o.x(), y() - o.y(), z() - o.z());
    }

    public Vector add(Vector o) {
        return new Vector(x() + o.x(), y() + o.y(), z() + o.z());
    }

    public double dot(Vector o) {
        return x() * o.x() + y() * o.y() + z() * o.z();
    }

    public double mag() {
        return Math.sqrt(this.dot(this));
    }

    public Vector norm() {
        double mag = mag();
        double div = mag == 0 ? Double.POSITIVE_INFINITY : 1 / mag;
        return this.mult(div);
    }

    public Vector cross(Vector o) {
        double x = (y() * o.z()) - (z() * o.y());
        double y = (z() * o.x()) - (x() * o.z());
        double z = (x() * o.y()) - (y() * o.x());
        return new Vector(x, y, z);
    }

    public boolean equals(Vector o) {
        return (x() == o.x() && y() == o.y() && z() == o.z());
    }

    @Override
    public String toString() {
        return "(" + x() + ", " + y() + ", " + z() + ")";
    }

}
