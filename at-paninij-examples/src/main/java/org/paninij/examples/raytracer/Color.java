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

public class Color extends Vector implements Serializable {
    private static final long serialVersionUID = 6821291418033385296L;
    public static final Color black = new Color(0, 0, 0);
    public static final Color background = new Color(.1, .3, .4);
    public static final Color plain = black;

    public Color(double x, double y, double z) {
        super(x, y, z);
    }

    public Color(double[] v) {
        super(v);
    }

    public Color(Vector v) {
        super(v);
    }

    public double red() {
        return x();
    }

    public double green() {
        return y();
    }

    public double blue() {
        return z();
    }

    @Override
    public String toString() {
        return "Color" + super.toString();
    }

    private int legalize(Double d) {
        int col = (int) (d * 255);
        return col > 255 ? 255 : col;
    }

    public int toRGB() {
        int nr = legalize(red());
        int ng = legalize(green());
        int nb = legalize(blue());
        return (255 << 24) | (nr << 16) | (ng << 8) | nb;
    }
}
