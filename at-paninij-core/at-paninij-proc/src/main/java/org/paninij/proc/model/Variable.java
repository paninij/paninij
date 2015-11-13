
package org.paninij.proc.model;

import javax.lang.model.type.TypeMirror;

public class Variable extends Type
{
    private String identifier;

    public Variable(TypeMirror mirror, String identifier) {
        super(mirror);
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public String toString() {
        return super.toString() + " " + this.identifier;
    }
}