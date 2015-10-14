package org.paninij.examples.raytracer;

import java.io.Serializable;

public class Light implements Serializable {
    private static final long serialVersionUID = -2128107150063310904L;
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
