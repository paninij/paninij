/*
 * This file is part of the Panini project at Iowa State University.
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributor(s): Dalton Mills
 */
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
    public final String realReturn;

    public MessageShape(Procedure procedure) {
        this.procedure = procedure;
        this.returnType = this.procedure.getReturnType();
        this.behavior = Behavior.determine(procedure);
        this.category = this.getCategory();
        this.encoded = this.encode();
        this.realReturn = this.getRealReturn();
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
                return Category.FUTURE;
            case NONE:
                return this.returnType.isVoid() ? Category.SIMPLE : Category.FUTURE;
            default:
                return Category.SIMPLE;
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
            // TODO return the exact premade class
            return "org.paninij.lang.*";
        case SIMPLE:
            return "org.paninij.runtime.messages";
        default:
            break;
        }
        throw new IllegalArgumentException("Message does not have a category, so it cannot fit into a package.");
    }

    private String getRealReturn() {

        switch (this.behavior) {
        case BLOCKED_FUTURE:
            return this.returnType.getMirror().toString();
        case BLOCKED_PREMADE:
            return this.returnType.getMirror().toString();
        case UNBLOCKED_DUCK:
            return this.returnType.getMirror().toString();
        case UNBLOCKED_FUTURE:
            return "java.util.concurrent.Future<" + this.returnType.wrapped() + ">";
        case UNBLOCKED_PREMADE:
            return this.returnType.getMirror().toString();
        case UNBLOCKED_SIMPLE:
            return this.returnType.getMirror().toString();
        case ERROR:
        default:
            throw new IllegalArgumentException("Message has an illegal (\"ERROR\") behavior, so the real return type cannot be determined.");
        }
    }

}
