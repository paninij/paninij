package org.paninij.examples.raytracer;

import java.io.Serializable;

public class Pixel implements Serializable {
    private static final long serialVersionUID = 5331379601064854383L;
    private int x;
    private int y;
    private Color color;

    public Pixel(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public Color color() {
        return color;
    }
}
