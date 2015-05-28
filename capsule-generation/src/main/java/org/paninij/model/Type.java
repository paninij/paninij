package org.paninij.model;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

public class Type
{

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

    public String wrapped() {
        switch (this.kind) {
        case BOOLEAN:
            return "Boolean";
        case BYTE:
            return "Byte";
        case SHORT:
            return "Short";
        case INT:
            return "Integer";
        case LONG:
            return "Long";
        case CHAR:
            return "Character";
        case FLOAT:
            return "Float";
        case DOUBLE:
            return "Double";
        case VOID:
            return "Void";
        case ARRAY:
        case DECLARED:  // A class or interface type.
            return this.kind.toString();
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

    @Override
    public String toString() {
        return this.mirror.toString();
    }
}
