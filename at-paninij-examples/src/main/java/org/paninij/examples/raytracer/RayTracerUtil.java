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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RayTracerUtil implements Serializable {
    private static final long serialVersionUID = -8995526731362416729L;
    private static int screenWidth = 640;
    private static int screenHeight = 480;
    private static int maxDepth;

    public static void initialize(int screenWidth, int screenHeight) {
        RayTracerUtil.screenWidth = screenWidth;
        RayTracerUtil.screenHeight = screenHeight;
        RayTracerUtil.maxDepth = 20;
    }

    public static int screenWidth() {
        return RayTracerUtil.screenWidth;
    }

    public static int screenHeight() {
        return RayTracerUtil.screenHeight;
    }

    public static int maxDepth() {
        return RayTracerUtil.maxDepth;
    }

    public static List<ISect> intersections(Ray ray, Scene scene) {
        List<ISect> isects = new ArrayList<ISect>();;
        for (SceneObject ob : scene.things()) {
            ISect isect = ob.intersect(ray);
            if (isect != null) {
                isects.add(isect);
            }
        }

        Collections.sort(isects, new Comparator<ISect>() {
            @Override
            public int compare(ISect o1, ISect o2) {
                 return o1.dist() < o2.dist() ? 1 : 0;
            }
        });

        return isects;
    }

    public static double testRay(Ray ray, Scene scene) {
        List<ISect> isects = intersections(ray, scene);
        return isects.isEmpty() ? 0 : isects.get(0).dist();
    }

    public static Color traceRay(Ray ray, Scene scene, int depth) {
        List<ISect> isects = intersections(ray, scene);
        return isects.isEmpty() ? Color.background : shade(isects.get(0), scene, depth);
    }

    public static Color getNaturalColor(SceneObject thing, Vector pos, Vector norm, Vector rd, Scene scene) {
        Color ret = Color.black;
        for (Light currentLight : scene.lights()) {
            Vector ldis = currentLight.position().sub(pos);
            Vector livec = ldis.norm();
            double neatIsect = testRay(new Ray(pos, livec), scene);
            boolean isInShadow = !((neatIsect > ldis.mag()) || neatIsect == 0);

            if (!isInShadow) {
                double illum = livec.dot(norm);
                Color lcolor = illum > 0 ? new Color(currentLight.color().mult(illum)) : Color.black;
                double specular = livec.dot(rd.norm());
                Color scolor;
                if (specular > 0) {
                    scolor = new Color(currentLight.color().mult(Math.pow(specular, thing.sface().roughness())));
                } else {
                    scolor = Color.black;
                }
                ret = new Color(ret.add((lcolor.mult(thing.sface().diffuse(pos)))).add(scolor.mult(thing.sface().specular(pos))));
            }
        }
        return ret;
    }

    public static Color getReflectionColor(SceneObject thing, Vector pos, Vector norm, Vector rd, Scene scene, int depth) {
        return new Color(traceRay(new Ray(pos, rd), scene, depth + 1).mult(thing.sface().reflect(pos)));
    }

    public static Color shade(ISect isect, Scene scene, int depth) {
        Vector d = isect.ray().dir();
        Vector pos = isect.ray().dir().mult(isect.dist()).add(isect.ray().start());
        Vector normal = isect.thing().normal(pos);
        Vector reflectDir = d.sub(normal.mult(2 * normal.dot(d)));
        Color ret = new Color(Color.plain.add(getNaturalColor(isect.thing(), pos, normal, reflectDir, scene)));
        if (depth >= maxDepth) return new Color(ret.add(new Color(0.5, 0.5, 0.5)));
        return new Color(ret.add(getReflectionColor(isect.thing(), pos.add(reflectDir.mult(0.001)), normal, reflectDir, scene, depth)));
    }

    public static double recenterX(double x) {
        return (x - (screenWidth / 2.0)) / (2.0 * screenWidth);
    }

    public static double recenterY(double y) {
        return -(y - (screenWidth / 2.0)) / (2.0 * screenHeight);
    }

    public static Vector getPoint(double x, double y, Camera camera) {
        return camera.forward().add(camera.right().mult(recenterX(x))).add(camera.up().mult(recenterY(y))).norm();
    }

}
