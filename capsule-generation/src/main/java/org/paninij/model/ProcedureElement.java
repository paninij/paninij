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
package org.paninij.model;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import org.paninij.apt.util.JavaModelInfo;
import org.paninij.apt.util.PaniniModelInfo;
import org.paninij.lang.Block;
import org.paninij.lang.Duck;
import org.paninij.lang.Future;

public class ProcedureElement implements Procedure
{
    private ExecutableElement element;
    private AnnotationKind annotationKind;
    private Type returnType;
    private String name;
    private List<Variable> parameters;

    public ProcedureElement(ExecutableElement e) {
        super();
        this.element = e;
        this.annotationKind = null;
        this.returnType = new Type(this.element.getReturnType());
        this.parameters = null;
        this.name = e.getSimpleName().toString();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public AnnotationKind getAnnotationKind() {
        if (this.annotationKind != null) return this.annotationKind;

        if (this.element.getAnnotation(Future.class) != null) {
            this.annotationKind = AnnotationKind.FUTURE;
        } else if (this.element.getAnnotation(Block.class) != null) {
            this.annotationKind = AnnotationKind.BLOCK;
        } else if (this.element.getAnnotation(Duck.class) != null) {
            this.annotationKind = AnnotationKind.DUCKFUTURE;
        } else {
            this.annotationKind = AnnotationKind.NONE;
        }

        return this.annotationKind;
    }

    @Override
    public Type getReturnType() {
        return this.returnType;
    }

    @Override
    public List<Variable> getParameters() {
        if (this.parameters != null) return this.parameters;

        this.parameters = new ArrayList<Variable>();

        for (VariableElement param : this.element.getParameters()) {
            Variable v = new Variable(param.asType(), param.toString());
            this.parameters.add(v);
        }

        return this.parameters;
    }

    @Override
    public String toString() {
        String str = this.getReturnType() + " " + this.getName() + "(";
        String args = "";
        for (Variable v : this.getParameters()) {
            args += (v + ", ");
        }
        args = args.length() > 1 ? args.substring(0, args.length() - 2) : "";
        str += args + ")";
        return str;
    }

    @Override
    public List<String> getModifiers() {
        List<String> modifiers = new ArrayList<String>();
        for (Modifier m : this.element.getModifiers()) {
            modifiers.add(m.toString());
        }
        return modifiers;
    }

    @Override
    public List<String> getThrown() {
        List<String> thrown = new ArrayList<String>();
        for (TypeMirror m : this.element.getThrownTypes()) {
            thrown.add(m.toString());
        }
        return thrown;
    }

}
