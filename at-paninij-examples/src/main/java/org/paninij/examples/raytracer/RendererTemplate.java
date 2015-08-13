package org.paninij.examples.raytracer;

import java.awt.image.BufferedImage;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;
import org.paninij.lang.Wired;

@Capsule public class RendererTemplate {
    @Child Tracer[] tracers = new Tracer[16];
    int screenWidth;
    int screenHeight;
    int chunkSize;

    public void init() {
        screenWidth = Raytracer.screenWidth();
        screenHeight = Raytracer.screenHeight();
        chunkSize = screenHeight / 16;
    }

    public BufferedImage render(Scene scene) {
        BufferedImage image = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);

    }
}
