package org.paninij.examples.raytracer;

import java.awt.image.BufferedImage;

public class Image {
    BufferedImage img;

    public Image() {
        img = null;
    }

    public Image(BufferedImage img) {
        this.img = img;
    }

    public BufferedImage buffered() {
        return img;
    }
}
