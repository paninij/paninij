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
	public enum UnduckableReason {
		NONE,
		IN_DEFAULT_PACKAGE,
		IS_FINAL,
		IS_VOID,
		IS_PRIMITIVE,
		IS_ARRAY,
		NO_ZERO_ARG_CONSTRUCTOR
	}

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
            throw new RuntimeException(String.format(
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

    public Duckability getDuckability() {
        // TODO need to fully determine duckability!
        // see https://github.com/hridesh/panini/wiki/Enumerating-Consequences-of-a-Procedure's-Properties-Along-Three-Dimensions

        if (PaniniModel.isPaniniCustom(this.mirror)) {
            return Duckability.DUCKED;
        }

        if (whyUnduckable() != UnduckableReason.NONE) {
        	return Duckability.UNDUCKABLE;
        }

        return Duckability.DUCKABLE;
    }
    
    public UnduckableReason whyUnduckable() {
    	if (JavaModel.isFinalType(this.mirror)) {
            return UnduckableReason.IS_FINAL;
        }

        if (JavaModel.isVoidType(this.mirror)) {
            return UnduckableReason.IS_VOID;
        }

        if (JavaModel.isPrimitive(this.mirror)) {
            return UnduckableReason.IS_PRIMITIVE;
        }

        if (JavaModel.isArray(this.mirror)) {
            return UnduckableReason.IS_ARRAY;
        }
        
        if (!JavaModel.hasZeroArgConstructor(this.mirror)) {
        	return UnduckableReason.NO_ZERO_ARG_CONSTRUCTOR;
        }
        
        if (JavaModel.getPackage(this.mirror).equals("")) {
        	return UnduckableReason.IN_DEFAULT_PACKAGE;
        }
        
        return UnduckableReason.NONE;
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
            throw new RuntimeException("Cannot wrap " + this.kind);
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
            throw new RuntimeException("Cannot slot " + this.kind);
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
