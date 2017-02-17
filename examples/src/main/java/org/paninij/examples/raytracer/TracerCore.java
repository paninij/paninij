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

import org.paninij.lang.Capsule;
import org.paninij.lang.Future;
import org.paninij.lang.Imported;

@Capsule class TracerCore {

	@Imported int chunk;
    @Imported int chunkHeight;
    @Imported int screenWidth;
    @Imported int screenHeight;

    @Future
    Pixel[] renderChunk(Scene scene) {
        int pixelCount = chunkHeight * screenWidth;
        int startHeight = chunk * chunkHeight;
        int endHeight = startHeight + chunkHeight;
        Pixel[] pixels = new Pixel[pixelCount];
        int indx = 0;

        Camera cam = scene.camera();
        Vector pos = cam.position();

        for (int i = startHeight; i < endHeight; i++) {
            for (int j = 0; j < screenWidth; j++) {
                Vector point = RayTracerUtil.getPoint(j, i, cam);
                Color col = RayTracerUtil.traceRay(new Ray(pos, point), scene, 0);
                pixels[indx++] = new Pixel(j, i, col);
            }
        }

        return pixels;
    }
}
