package org.paninij.examples.raytracer;

import java.awt.image.BufferedImage;

public class Image
{
    private BufferedImage img;

    public Image() {
        img = null;
    }

    public Image(BufferedImage img) {
        this.img = img;
    }

    public BufferedImage bufferedImage() {
        return img;
    }
}
