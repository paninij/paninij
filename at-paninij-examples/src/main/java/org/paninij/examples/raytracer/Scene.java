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
import java.util.ArrayList;
import java.util.List;

public class Scene implements Serializable
{
    private static final long serialVersionUID = -7075103757217435908L;
    private List<SceneObject> things;
    private List<Light> lights;
    private Camera camera;

    public Scene() {
        this.things = new ArrayList<SceneObject>();
        this.lights = new ArrayList<Light>();
        this.camera = new Camera();
    }

    public Scene(List<SceneObject> things, List<Light> lights, Camera camera) {
        this.things = things;
        this.lights = lights;
        this.camera = camera;
    }

    public List<SceneObject> things() {
        return things;
    }

    public List<Light> lights() {
        return lights;
    }

    public Camera camera() {
        return camera;
    }
}
