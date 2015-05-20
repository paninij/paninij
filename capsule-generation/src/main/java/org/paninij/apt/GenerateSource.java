package org.paninij.apt;

import javax.annotation.processing.AbstractProcessor;

import org.paninij.model.Capsule;

public class GenerateSource {

    public AbstractProcessor context;

    public GenerateSource(AbstractProcessor context) {
        this.context = context;
    }

    public boolean GenerateCapsule(Capsule capsule) {
        return false;
    }
}
