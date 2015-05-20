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
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import org.paninij.apt.util.JavaModelInfo;
import org.paninij.apt.util.PaniniModelInfo;
import org.paninij.lang.Block;
import org.paninij.lang.Future;

public class ElementProcedure extends Procedure
{
    private ExecutableElement element;
    private TypeMirror returnType;
    private FutureType futureType;
    private TypeKind messageType;
    private String name;
    private List<Variable> parameters;
    private boolean isFinal;

    public ElementProcedure(ExecutableElement e) {
        super();
        this.element = e;
        this.returnType = this.element.getReturnType();
        this.futureType = null;
        this.messageType = null;
        this.parameters = null;
        this.name = e.getSimpleName().toString();
        this.isFinal = JavaModelInfo.isFinalType(this.returnType);
    }

    @Override
    public FutureType getFutureType() {
        if (this.futureType != null) return this.futureType;

        if (this.element.getAnnotation(Future.class) != null) {
            this.futureType = FutureType.FUTURE;
        } else if (this.element.getAnnotation(Block.class) != null) {
            this.futureType = FutureType.BLOCK;
        } else {
            if (this.isFinal || this.getMessageType() == TypeKind.PRIMITIVE) {
                this.futureType = FutureType.BLOCK;
            } else {
                this.futureType = FutureType.DUCKFUTURE;
            }
        }
        return this.futureType;
    }

    @Override
    public boolean isReturnTypeFinal() {
        return this.isFinal;
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
    public String getName() {
        return this.name;
    }

    @Override
    public TypeMirror getReturnType() {
        return this.returnType;
    }

    @Override
    public TypeKind getMessageType() {
        if (this.messageType != null) return this.messageType;

        if (JavaModelInfo.hasVoidReturnType(this.element)) {
            this.messageType = TypeKind.VOID;
        } else if (JavaModelInfo.hasPrimitiveReturnType(this.element)) {
            this.messageType = TypeKind.PRIMITIVE;
        } else if (JavaModelInfo.isArray(this.returnType)) {
            this.messageType = TypeKind.ARRAY;
        } else if (PaniniModelInfo.isPaniniCustom(this.returnType)) {
            this.messageType = TypeKind.PANINICUSTOM;
        } else {
            this.messageType = TypeKind.NORMAL;
        }

        return this.messageType;
    }
}
