package org.paninij.model;

import javax.lang.model.type.TypeMirror;

public class Variable
{
    private TypeMirror type;
    private String identifier;

    public Variable(TypeMirror type, String identifier) {
        this.type = type;
        this.identifier = identifier;
    }

    public TypeMirror getType() {
        return this.type;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public String toString() {
        return this.type.toString() + " " + this.identifier;
    }
}