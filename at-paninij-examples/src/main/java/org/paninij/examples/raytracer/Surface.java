package org.paninij.examples.raytracer;

public abstract class Surface {
    public abstract Color diffuse(Vector v);
    public abstract Color specular(Vector v);
    public abstract Double reflect(Vector v);
    public abstract Double roughness();
}
