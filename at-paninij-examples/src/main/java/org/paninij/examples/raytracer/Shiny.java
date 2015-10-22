package org.paninij.examples.raytracer;

import java.io.Serializable;

public class Shiny extends Surface implements Serializable {
    private static final long serialVersionUID = 6847733167805058111L;

    private Color color;

    public Shiny() {
        this.color = new Color(1, 1, 1);
    }

    public Shiny(Color color) {
        this.color = color;
    }

    @Override
    public Color diffuse(Vector v) {
        return color;
    }

    @Override
    public Color specular(Vector v) {
        return new Color(this.color.mult(.5));
    }

    @Override
    public Double reflect(Vector v) {
        return 0.26;
    }

    @Override
    public Double roughness() {
        return 50.0;
    }

}
