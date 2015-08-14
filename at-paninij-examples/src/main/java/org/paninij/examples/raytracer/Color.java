package org.paninij.examples.raytracer;

import java.io.Serializable;

public class Color extends Vector implements Serializable {
    private static final long serialVersionUID = 6821291418033385296L;
    public static final Color black = new Color(0, 0, 0);
    public static final Color background = black;
    public static final Color plain = black;

    public Color(double x, double y, double z) {
        super(x, y, z);
    }

    public Color(double[] v) {
        super(v);
    }

    public Color(Vector v) {
        super(v);
    }

    public double red() {
        return x();
    }

    public double green() {
        return y();
    }

    public double blue() {
        return z();
    }

    @Override
    public String toString() {
        return "Color" + super.toString();
    }

    private int legalize(Double d) {
        int col = (int) (d * 255);
        return col > 255 ? 255 : col;
    }

    public int toRGB() {
        int nr = legalize(red());
        int ng = legalize(green());
        int nb = legalize(blue());
        return (255 << 24) | (nr << 16) | (ng << 8) | nb;
    }
}
