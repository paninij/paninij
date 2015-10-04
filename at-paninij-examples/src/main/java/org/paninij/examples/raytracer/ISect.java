package org.paninij.examples.raytracer;

import java.io.Serializable;

public class ISect implements Serializable {
    private static final long serialVersionUID = 6601281940824842142L;
    private SceneObject thing;
    private Ray ray;
    private double dist;

    public ISect(SceneObject thing, Ray ray, double dist) {
        this.thing = thing;
        this.ray = ray;
        this.dist = dist;
    }

    public SceneObject thing() {
        return thing;
    }

    public Ray ray() {
        return ray;
    }

    public double dist() {
        return dist;
    }
}
