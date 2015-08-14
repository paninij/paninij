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
            System.out.println("wire " + i + " " + chunkHeight + " " + screenWidth + " " + screenHeight);
            tracers[i].wire(i, chunkHeight, screenWidth, screenHeight);
        }
    }

    public Image render(Scene scene) {
        BufferedImage image = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);

        List<Pixel[]> pixels = new ArrayList<Pixel[]>();


        for (Tracer tracer : tracers) pixels.add(tracer.renderChunk(scene));

        System.out.println("done creating image");

        for (Pixel[] chunk : pixels) {
            for (Pixel p : chunk) {
                System.out.println("Chunk... " + p.x() + ", " + p.y() + " = " + p.color());
                image.setRGB(p.x(), p.y(), p.color().toRGB());
            }
        }



        return new Image(image);
    }
}
