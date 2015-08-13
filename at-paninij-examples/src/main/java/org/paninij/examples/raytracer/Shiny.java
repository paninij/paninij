package org.paninij.examples.raytracer;

public class Shiny extends Surface {

    @Override
    public Color diffuse(Vector v) {
        return new Color(1, 1, 1);
    }

    @Override
    public Color specular(Vector v) {
        return new Color(0.5, 0.5, 0.5);
    }

    @Override
    public Double reflect(Vector v) {
        return 0.6;
    }

    @Override
    public Double roughness() {
        return 50.0;
    }

}
