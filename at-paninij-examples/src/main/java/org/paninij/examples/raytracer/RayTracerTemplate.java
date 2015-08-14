package org.paninij.examples.raytracer;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule public class RayTracerTemplate {
    @Child Renderer renderer;
    @Child SceneData sceneData;
    @Child UserInterface ui;

    public void init() {
        RayTracerUtil.initialize(640, 480);
    }

    public void run() {
        Scene scene = sceneData.getScene();
        Image img = renderer.render(scene);
        ui.draw(img);
    }

}
