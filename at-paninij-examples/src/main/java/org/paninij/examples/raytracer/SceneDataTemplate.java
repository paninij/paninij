package org.paninij.examples.raytracer;

import java.util.ArrayList;
import java.util.List;

import org.paninij.lang.Capsule;

@Capsule public class SceneDataTemplate {

    Scene scene;

    public void init() {
        Camera camera = new Camera(new Vector(3, 2, 4), new Vector(-1, 0.5, 0));
        List<SceneObject> things = new ArrayList<SceneObject>();
        List<Light> lights = new ArrayList<Light>();

        things.add(new Sphere(Surfaces.shinyRed, new Vector(0, 2.2, 0), .5));
        things.add(new Sphere(Surfaces.shiny, new Vector(-1, 1.5, 1.5), .5));
        things.add(new Plane(Surfaces.checkerBoard, new Vector(0, 1, 0), 0));

        lights.add(new Light(new Vector(-1, 2.5, 0), new Color(0.49, 0.49, 0.49)));
        lights.add(new Light(new Vector(1.5, 2.5, 1.5), new Color(0.49, 0.49, 0.49)));
        lights.add(new Light(new Vector(1.5, 2.5, -1.5), new Color(0.49, 0.49, 0.49)));
        lights.add(new Light(new Vector(0, 3.5, 0), new Color(0.25, 0.25, 0.85)));

        scene = new Scene(things, lights, camera);
    }

    public Scene getScene() {
//        Scene sceneCopy = (Scene) DeepCopy.copy(scene);
//        return sceneCopy;
        return scene;
    }

}
