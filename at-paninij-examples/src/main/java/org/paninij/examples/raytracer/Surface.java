package org.paninij.examples.raytracer;

import java.io.Serializable;

public abstract class Surface implements Serializable {
    private static final long serialVersionUID = -5690624396407030829L;
    public abstract Color diffuse(Vector v);
    public abstract Color specular(Vector v);
    public abstract Double reflect(Vector v);
    public abstract Double roughness();
}
