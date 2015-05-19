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

public class ElementProcedure extends Procedure
{
    private ExecutableElement element;
    private TypeMirror returnType;
    private String name;
    private TypeKind messageType;
    private List<Variable> parameters;

    public ElementProcedure(ExecutableElement e) {
        super();
        this.element = e;
        this.name = e.getSimpleName().toString();
        this.returnType = this.element.getReturnType();

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

        this.parameters = new ArrayList<Variable>();

        for (VariableElement param : this.element.getParameters()) {
            Variable v = new Variable(param.asType(), param.toString());
            this.parameters.add(v);
        }

    }

    @Override
    public boolean shouldBlock() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isReturnTypeFinal() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<Variable> getParameters() {
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
        return this.messageType;
    }

}
