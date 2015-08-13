package org.paninij.examples.raytracer;

public class Plane extends SceneObject {
    private Surface sface;
    private Vector n;
    private double offset;

    public Plane(Surface sface, Vector n, double offset) {
        this.sface = sface;
        this.n = n;
        this.offset = offset;
    }

    @Override
    public ISect intersect(Ray ray) {
        Double denom = n.dot(ray.dir());
        if (denom > 0 || denom.equals(Double.NaN)) {
            return null;
        }
        Double dist = (n.dot(ray.start()) + offset) / (-denom);
        return dist.equals(Double.NaN) ? null : new ISect(this, ray, dist);
    }

    @Override
    public Vector normal(Vector pos) {
        return n;
    }

    @Override
    public Surface sface() {
        return sface;
    }

}
