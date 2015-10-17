package org.paninij.examples.raytracer;

import java.io.Serializable;

public abstract class SceneObject implements Serializable {
    private static final long serialVersionUID = 7764382816353479572L;
    public abstract ISect intersect(Ray ray);
    public abstract Vector normal(Vector pos);
    public abstract Surface sface();
}
