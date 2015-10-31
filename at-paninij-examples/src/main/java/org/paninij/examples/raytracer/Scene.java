package org.paninij.examples.raytracer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Scene implements Serializable
{
    private static final long serialVersionUID = -7075103757217435908L;
    private List<SceneObject> things;
    private List<Light> lights;
    private Camera camera;

    public Scene() {
        this.things = new ArrayList<SceneObject>();
        this.lights = new ArrayList<Light>();
        this.camera = new Camera();
    }

    public Scene(List<SceneObject> things, List<Light> lights, Camera camera) {
        this.things = things;
        this.lights = lights;
        this.camera = camera;
    }

    public List<SceneObject> things() {
        return things;
    }

    public List<Light> lights() {
        return lights;
    }

    public Camera camera() {
        return camera;
    }
}
