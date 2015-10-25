package org.paninij.examples.raytracer;


import org.paninij.lang.Capsule;

@Capsule public class UserInterfaceTemplate {

    int screenWidth;
    int screenHeight;

    MainFrame frame;

    public void init() {
        screenWidth = RayTracerUtil.screenWidth();
        screenHeight = RayTracerUtil.screenHeight();
        frame = new MainFrame("Raytracer", screenWidth, screenHeight);
    }

    public void draw(Image img) {
        frame.setImage(img.bufferedImage());
    }

}
