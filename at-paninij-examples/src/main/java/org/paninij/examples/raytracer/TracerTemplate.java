package org.paninij.examples.raytracer;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

@Capsule public class TracerTemplate {

	@Imports int chunk;
    @Imports int chunkHeight;
    @Imports int screenWidth;
    @Imports int screenHeight;

    public Pixel[] renderChunk(Scene scene) {
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
