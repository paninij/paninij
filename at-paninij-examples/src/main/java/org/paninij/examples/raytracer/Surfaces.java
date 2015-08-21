package org.paninij.examples.raytracer;

import java.io.Serializable;

public class Surfaces implements Serializable {
    private static final long serialVersionUID = -178010952350167505L;
    public static final Surface checkerBoard = new CheckerBoard();
    public static final Surface shiny = new Shiny();
    public static final Surface shinyRed = new Shiny(new Color(2, 1, 1));
}
