package org.paninij.examples.raytracer;

public class Camera {
    private Vector position;
    private Vector lookAt;

    public Camera(Vector position, Vector lookAt) {
        this.position = position;
        this.lookAt = lookAt;
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
