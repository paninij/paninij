package org.paninij.examples.raytracer;

import java.io.Serializable;

public class Ray implements Serializable {
    private static final long serialVersionUID = -2503905010508031929L;
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
