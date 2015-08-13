package org.paninij.examples.raytracer;

public class Ray {
    private Vector start;
    private Vector dir;

    public Ray(Vector start, Vector dir) {
        this.start = start;
        this.dir = dir;
    }

    public Vector start() {
        return start;
    }

    public Vector dir() {
        return dir;
    }
}
