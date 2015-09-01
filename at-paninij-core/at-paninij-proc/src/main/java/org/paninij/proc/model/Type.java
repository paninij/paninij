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
package org.paninij.proc.model;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.paninij.proc.util.JavaModel;
import org.paninij.proc.util.PaniniModel;

public class Type
{

    public enum Duckability {
        UNDUCKABLE,
        DUCKABLE,
        DUCKED
    }

    public enum Category {
        NORMAL,
        VOID,
        PRIMITIVE
    }

    private TypeMirror mirror;
    private TypeKind kind;

    public Type(TypeMirror mirror) {
        this.mirror = mirror;
        this.kind = mirror.getKind();
    }

    public TypeMirror getMirror() {
        return this.mirror;
    }

    public TypeKind getKind() {
        return this.kind;
    }

    public String encodeFull() {
        String enc = this.mirror.toString().replaceAll("_", "__").replaceAll("\\.", "_");
        if (this.kind == TypeKind.ARRAY) {
            enc = enc.replace('[', '$');
            enc = enc.replace(']', ' ');
            enc = enc.replaceAll(" ", "array");
        }
        return enc;
    }

    public String encode() {
        switch (this.kind) {
        case ARRAY:
        case DECLARED:
            return "ref";
        case BOOLEAN:
            return "bool";
        case BYTE:
            return "byte";
        case CHAR:
            return "char";
        case DOUBLE:
            return "dbl";
        case FLOAT:
            return "float";
        case INT:
            return "int";
        case LONG:
            return "long";
        case SHORT:
            return "short";
        default:
            throw new IllegalArgumentException(String.format(
                    "The `variable` (of the form `%s`) has an unexpected and un-encodable `TypeKind`: %s",
                    this, this.kind));
        }
    }

    public Category getCategory() {
        if (JavaModel.isVoidType(this.mirror)) {
            return Category.VOID;
        }

        if (JavaModel.isPrimitive(this.mirror)) {
            return Category.PRIMITIVE;
        }

        return Category.NORMAL;
    }

    public Duckability getDuckability(){
        // TODO need to fully determine duckability!
        // see https://github.com/hridesh/panini/wiki/Enumerating-Consequences-of-a-Procedure's-Properties-Along-Three-Dimensions

        if (PaniniModel.isPaniniCustom(this.mirror)) {
            return Duckability.DUCKED;
        }

        if (JavaModel.isFinalType(this.mirror)) {
            return Duckability.UNDUCKABLE;
        }

        if (JavaModel.isVoidType(this.mirror)) {
            return Duckability.UNDUCKABLE;
        }

        if (JavaModel.isPrimitive(this.mirror)) {
            return Duckability.UNDUCKABLE;
        }

        if (JavaModel.isArray(this.mirror)) {
            return Duckability.UNDUCKABLE;
        }

        return Duckability.DUCKABLE;
    }

    public String wrapped() {
        switch (this.kind) {
        case BOOLEAN:
            return "java.lang.Boolean";
        case BYTE:
            return "java.lang.Byte";
        case SHORT:
            return "java.lang.Short";
        case INT:
            return "java.lang.Integer";
        case LONG:
            return "java.lang.Long";
        case CHAR:
            return "java.lang.Character";
        case FLOAT:
            return "java.lang.Float";
        case DOUBLE:
            return "java.lang.Double";
        case VOID:
            return "java.lang.Void";
        case ARRAY:
        case DECLARED:  // A class or interface type.
            return this.mirror.toString();
        case NONE:
        case NULL:
        case ERROR:
        case TYPEVAR:
        case WILDCARD:
        case PACKAGE:
        case EXECUTABLE:
        case OTHER:
        case UNION:
        case INTERSECTION:
        default:
            System.out.println("DEFAULT: " + this.mirror.toString() + " " + this.kind);
            throw new IllegalArgumentException();
        }
    }

    public String packed() {
        if (this.isArray()) {
            ArrayType t = (ArrayType) this.mirror;
            Type comp = new Type(t.getComponentType());
            return comp.packed();
        }
        return this.wrapped();
    }

    public String raw() {
        return this.mirror.toString();
    }

    public Type getEncapsulatedType() {
        if (this.isArray()) {
            ArrayType t = (ArrayType) this.mirror;
            Type comp = new Type(t.getComponentType());
            return comp.getEncapsulatedType();
        }
        return this;
    }

    public String slot() {
        switch (this.kind) {
        case BOOLEAN:
            return "boolean";
        case BYTE:
            return "byte";
        case SHORT:
            return "short";
        case INT:
            return "int";
        case LONG:
            return "long";
        case CHAR:
            return "char";
        case FLOAT:
            return "float";
        case DOUBLE:
            return "double";
        case ARRAY:
        case DECLARED:  // A class or interface type.
            return "Object";
        case VOID:
        case NONE:
        case NULL:
        case ERROR:
        case TYPEVAR:
        case WILDCARD:
        case PACKAGE:
        case EXECUTABLE:
        case OTHER:
        case UNION:
        case INTERSECTION:
        default:
            throw new IllegalArgumentException();
        }
    }

    public boolean isVoid() {
        return this.kind.equals(TypeKind.VOID);
    }

    public boolean isArray() {
        return this.kind.equals(TypeKind.ARRAY);
    }

    public boolean isPrimitive() {
        // TODO move away from JavaModelInfo
        return JavaModel.isPrimitive(this.mirror);
    }

    public boolean isInterface() {
        if (this.kind == TypeKind.DECLARED) {
            DeclaredType comp = (DeclaredType) this.mirror;
            TypeElement t = (TypeElement) comp.asElement();
            return t.getKind() == ElementKind.INTERFACE;
        }
        return false;
    }

    public boolean isCapsule() {
        // TODO checking if the type is actually a capsule.
        // If a capsule has already been compiled, this method will fail!
        return this.kind == TypeKind.ERROR;
    }

    @Override
    public String toString() {
        return this.raw();
    }
}
