/*******************************************************************************
 * This file is part of the Panini project at Iowa State University.
 *
 * @PaniniJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * @PaniniJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with @PaniniJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributors:
 * 	Dr. Hridesh Rajan,
 * 	Dalton Mills,
 * 	David Johnston,
 * 	Trey Erenberger
 *******************************************************************************/

package org.paninij.proc.util;

import java.util.ArrayList;
import java.util.List;

import org.paninij.proc.model.Behavior;
import org.paninij.proc.model.Procedure;
import org.paninij.proc.model.Type;
import org.paninij.proc.model.Variable;

public class MessageShape
{
    public final Procedure procedure;

    public final Type returnType;
    public final Category category;
    public final Behavior behavior;
    public final String encoded;
    public final String realReturn;
    public final String kindAnnotation;

    public MessageShape(Procedure procedure) {
        this.procedure = procedure;
        this.returnType = this.procedure.getReturnType();
        this.behavior = Behavior.determine(procedure);
        this.category = this.getCategory();
        this.encoded = this.encode();
        this.realReturn = this.getRealReturn();
        this.kindAnnotation = this.getKindAnnotation();
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
        	return this.returnType.encodeFull();
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
    
    public String fullLocation() {
    	if (this.category == Category.PREMADE) return this.returnType.raw();
    	return this.getPackage() + "." + this.encoded;
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
    
    private String getKindAnnotation() {
        String procedureType = "";
        
        switch (this.behavior) {
        case BLOCKED_FUTURE:
        case BLOCKED_PREMADE:
            procedureType = "@org.paninij.lang.Block";
            break;
        case UNBLOCKED_FUTURE:
            procedureType = "@org.paninij.lang.Future";
            break;
        case UNBLOCKED_PREMADE:
        case UNBLOCKED_DUCK:
            procedureType = "@org.paninij.lang.Duck";
        case UNBLOCKED_SIMPLE:
        case ERROR:
        default:
        }
        
        return procedureType;
    }

}
