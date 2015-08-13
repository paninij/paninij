package org.paninij.examples.raytracer;

import org.paninij.lang.Capsule;

@Capsule public class TracerTemplate {

    public Pixel[] renderChunk(Scene s) {

        int chunkSize = Raytracer.screenHeight() / 16;

        Pixel[] pixels = new Pixel[chunkSize];


        return pixels;
    }

}
