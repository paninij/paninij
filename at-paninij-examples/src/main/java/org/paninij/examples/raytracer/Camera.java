package org.paninij.examples.raytracer;

import java.io.Serializable;

public class Camera implements Serializable {
    private static final long serialVersionUID = 4285425187179729621L;
    private Vector position;
    private Vector lookAt;

    public Camera(Vector position, Vector lookAt) {
        this.position = position;
        this.lookAt = lookAt;
    }

    public Camera() {
        this.position = new Vector(0, 0, 0);
        this.lookAt = new Vector(0, 0, 0);
    }

    public Vector position() {
        return position;
    }

    public Vector down() {
        return new Vector(0, -1, 0);
    }

    public Vector forward() {
        return lookAt.sub(position).norm();
    }

    public Vector right() {
        return forward().cross(down()).norm().mult(1.5);
    }

    public Vector up() {
        return forward().cross(right()).norm().mult(1.5);
    }
}
