package org.paninij.examples.raytracer;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;

@Capsule public class RayTracerTemplate {
    @Local Renderer renderer;
    @Local SceneData sceneData;
    @Local UserInterface ui;

    public void init() {
        RayTracerUtil.initialize(640, 480);
    }

    public void run() {
        Scene scene = sceneData.getScene();
        Image img = renderer.render(scene);
        ui.draw(img);
    }

}
