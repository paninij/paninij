package org.paninij.examples.raytracer;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule public class RendererTemplate {

    int CHUNKS = 16;
    @Child Tracer[] tracers = new Tracer[CHUNKS];
    int screenWidth = 640;
    int screenHeight = 480;
    int chunkHeight = screenHeight / CHUNKS;

    public void init() {
        screenWidth = 640;
        screenHeight = 480;
        chunkHeight = screenHeight / CHUNKS;
    }

    public void design(Renderer self) {
        for (int i = 0; i < tracers.length; i++) {
            tracers[i].wire(i, chunkHeight, screenWidth, screenHeight);
        }
    }

    public Image render(Scene scene) {

        BufferedImage image = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);

        List<Pixel[]> chunks = new ArrayList<Pixel[]>();

        for (Tracer tracer : tracers) chunks.add(tracer.renderChunk(scene));

        for (Pixel[] chunk : chunks) {
            for (int i = 0; i < chunk.length; i ++) {
                Pixel p = chunk[i];
                image.setRGB(p.x(), p.y(), p.color().toRGB());
            }
        }

        return new Image(image);
    }
}
