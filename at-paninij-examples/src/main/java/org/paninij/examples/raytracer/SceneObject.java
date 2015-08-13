package org.paninij.examples.raytracer;

public abstract class SceneObject {
    public abstract ISect intersect(Ray ray);
    public abstract Vector normal(Vector pos);
    public abstract Surface sface();
}
