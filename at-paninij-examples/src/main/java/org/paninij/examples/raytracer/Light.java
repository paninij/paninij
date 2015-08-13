package org.paninij.examples.raytracer;

public class Light {
    private Vector position;
    private Color color;

    public Light(Vector position, Color color) {
        this.position = position;
        this.color = color;
    }

    public Vector position() {
        return position;
    }

    public Vector color() {
        return color;
    }
}
