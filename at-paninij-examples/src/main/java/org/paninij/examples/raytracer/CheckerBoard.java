package org.paninij.examples.raytracer;

public class CheckerBoard extends Surface {

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
        return (Math.floor(v.z()) + Math.floor(v.x())) % 2 != 0 ? 0.1 : 0.7;
    }

    @Override
    public Double roughness() {
        return 150.0;
    }

}
