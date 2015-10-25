package org.paninij.examples.raytracer;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;

@Capsule public class RendererTemplate {

    int CHUNKS = 16;
    @Local Tracer[] tracers = new Tracer[CHUNKS];
    int screenWidth = 640;
    int screenHeight = 480;
    int chunkHeight = screenHeight / CHUNKS;

    public void init() {
        chunkHeight = screenHeight / CHUNKS;
    }

    public void design(Renderer self) {
        for (int i = 0; i < tracers.length; i++) {
            tracers[i].imports(i, chunkHeight, screenWidth, screenHeight);
        }
    }

    public Image render(Scene scene) {
        BufferedImage image = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
        List<Pixel[]> chunks = new ArrayList<Pixel[]>();

        try {
            for (Tracer tracer : tracers) {
                chunks.add(tracer.renderChunk(scene).get());
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        for (Pixel[] chunk : chunks) {
            for (int i = 0; i < chunk.length; i ++) {
                Pixel p = chunk[i];
                image.setRGB(p.x(), p.y(), p.color().toRGB());
            }
        }

        return new Image(image);
    }
}
