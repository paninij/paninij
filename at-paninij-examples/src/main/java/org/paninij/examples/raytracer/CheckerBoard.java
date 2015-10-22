package org.paninij.examples.raytracer;

import java.io.Serializable;

public class CheckerBoard extends Surface implements Serializable {
    private static final long serialVersionUID = 2694218922038717047L;

    @Override
    public Color diffuse(Vector v) {
        return (Math.floor(v.z()) + Math.floor(v.x())) % 2 != 0 ? new Color(1, 1, 1) : new Color(0, 0, 0);
    }

    @Override
    public Color specular(Vector v) {
        return new Color(1, 1, 1);
    }

    @Override
    public Double reflect(Vector v) {
        return (Math.floor(v.z()) + Math.floor(v.x())) % 2 != 0 ? 0.3 : 0.3;
    }

    @Override
    public Double roughness() {
        return 15.0;
    }

}
