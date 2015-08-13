package org.paninij.examples.raytracer;

public class ISect {

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
