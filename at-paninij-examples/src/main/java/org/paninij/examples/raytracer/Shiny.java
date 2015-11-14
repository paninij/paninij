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

public class Shiny extends Surface implements Serializable {
    private static final long serialVersionUID = 6847733167805058111L;

    private Color color;

    public Shiny() {
        this.color = new Color(1, 1, 1);
    }

    public Shiny(Color color) {
        this.color = color;
    }

    @Override
    public Color diffuse(Vector v) {
        return color;
    }

    @Override
    public Color specular(Vector v) {
        return new Color(this.color.mult(.5));
    }

    @Override
    public Double reflect(Vector v) {
        return 0.26;
    }

    @Override
    public Double roughness() {
        return 50.0;
    }

}
