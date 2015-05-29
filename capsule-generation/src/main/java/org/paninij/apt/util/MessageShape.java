package org.paninij.apt.util;

import java.util.ArrayList;
import java.util.List;

import org.paninij.model.Behavior;
import org.paninij.model.Procedure;
import org.paninij.model.Type;
import org.paninij.model.Variable;

public class MessageShape
{
    public final Procedure procedure;

    public final Type returnType;
    public final Category category;
    public final Behavior behavior;
    public final String encoded;

    public MessageShape(Procedure procedure) {
        this.procedure = procedure;
        this.returnType = this.procedure.getReturnType();
        this.behavior = Behavior.determine(procedure);
        this.category = this.getCategory();
        this.encoded = this.encode();
    }

    public enum Category {
        SIMPLE,
        FUTURE,
        DUCKFUTURE,
        PREMADE,
        ERROR
    }

    private Category getCategory() {
        switch (this.returnType.getDuckability()) {
        case DUCKED:
            return Category.PREMADE;
        case DUCKABLE:
            switch (this.procedure.getAnnotationKind()) {
            case FUTURE:
            case BLOCK:
                return Category.FUTURE;
            case DUCKFUTURE:
            case NONE:
            default:
                return Category.DUCKFUTURE;
            }
        case UNDUCKABLE:
            switch (this.procedure.getAnnotationKind()) {
            case DUCKFUTURE:
                throw new IllegalArgumentException("Procedure labelled with @Duck is unduckable.");
            case FUTURE:
            case BLOCK:
            case NONE:
            default:
                return Category.FUTURE;
            }
        default:
            // this should be unreachable
            return Category.FUTURE;
        }
    }

    private String encode() {
        switch (this.category) {
        case SIMPLE:
            return this.returnType.encodeFull() + "$Simple$" + this.encodeParameters();
        case FUTURE:
            return this.returnType.encodeFull() + "$Future$" + this.encodeParameters();
        case DUCKFUTURE:
            return this.returnType.encodeFull() + "$Duck$" + this.encodeParameters();
        case PREMADE:
        default:
            // premade still get encoded so we can keep track of all ducks used in the system
            return this.returnType.encodeFull() + "$Premade$" + this.encodeParameters();
        }
    }

    private String encodeParameters() {
        List<String> slots = new ArrayList<String>();
        for (Variable v : this.procedure.getParameters()) {
            slots.add(v.encode());
        }
        return String.join("$", slots);
    }

    public String getPackage() {
        switch (this.category) {
        case DUCKFUTURE:
            return "org.paninij.runtime.ducks";
        case FUTURE:
        case ERROR:
            return "org.paninij.runtime.futures";
        case PREMADE:
            return "org.paninij.lang";
        case SIMPLE:
            return "org.paninij.runtime.messages";
        default:
            break;
        }
        throw new IllegalArgumentException("Message does not have a category, so it cannot fit into a package.");
    }

}
