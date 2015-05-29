package org.paninij.model;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.paninij.apt.util.JavaModelInfo;
import org.paninij.apt.util.PaniniModelInfo;

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
        return this.mirror.toString().replaceAll("_", "__").replaceAll("\\.", "_");
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
        if (JavaModelInfo.isVoidType(this.mirror)) {
            return Category.VOID;
        }

        if (JavaModelInfo.isPrimitive(this.mirror)) {
            return Category.PRIMITIVE;
        }

        return Category.NORMAL;
    }

    public Duckability getDuckability(){
        // TODO need to fully determine duckability!
        // see https://github.com/hridesh/panini/wiki/Enumerating-Consequences-of-a-Procedure's-Properties-Along-Three-Dimensions

        if (PaniniModelInfo.isPaniniCustom(this.mirror)) {
            return Duckability.DUCKED;
        }

        if (JavaModelInfo.isFinalType(this.mirror)) {
            return Duckability.UNDUCKABLE;
        }

        if (JavaModelInfo.isVoidType(this.mirror)) {
            return Duckability.UNDUCKABLE;
        }

        if (JavaModelInfo.isPrimitive(this.mirror)) {
            return Duckability.UNDUCKABLE;
        }

        if (JavaModelInfo.isArray(this.mirror)) {
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
            throw new IllegalArgumentException();
        }
    }

    public boolean isVoid() {
        return this.kind.equals(TypeKind.VOID);
    }

    @Override
    public String toString() {
        return this.mirror.toString();
    }
}
