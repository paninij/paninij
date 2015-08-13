package org.paninij.examples.raytracer;

import java.util.List;

public class Scene {
    private List<SceneObject> things;
    private List<Light> lights;
    private Camera camera;

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
