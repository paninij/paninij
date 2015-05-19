package org.paninij.model;

import javax.lang.model.element.ExecutableElement;

public class Procedure
{
    private ExecutableElement element;

    public Procedure(ExecutableElement e) {
        this.element = e;
    }

    public String getName() {
        return "";
    }

}
