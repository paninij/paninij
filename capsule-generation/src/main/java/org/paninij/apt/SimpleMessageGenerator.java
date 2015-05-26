package org.paninij.apt;

import org.paninij.model.Procedure;

public class SimpleMessageGenerator
{
    PaniniProcessor context;

    public static void generate(PaniniProcessor context, Procedure procedure) {
        SimpleMessageGenerator generator = new SimpleMessageGenerator();
        generator.context = context;
        generator.generateSimple(procedure);
    }

    private void generateSimple(Procedure procedure) {
        //TODO
    }
}
