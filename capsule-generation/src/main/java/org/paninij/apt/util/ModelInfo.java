package org.paninij.apt.util;

import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

public class ModelInfo {

    /**
     * Gives a string representation of the executable element's return type. If the return type is
     * a primitive (e.g. int, double, etc.), then the returned type will be that primitive's boxed
     * type (e.g. Integer, Double, etc.).
     * 
     * In the case that the executable element return has no return type (i.e. `void`), then "void"
     * is returned.
     * 
     * An `IllegalArgumentException` is raised whenever the `TypeKind` of `exec`'s return value is
     * any of the following:
     * 
     *  - NONE
     *  - NULL
     *  - ERROR
     *  - WILDCARD
     *  - PACKAGE
     *  - EXECUTABLE
     *  - OTHER
     *  - UNION
     *  - INTERSECTION
     * 
     * @param exec
     * @return A string of the executable's return type.
     */
    public static String getBoxedReturnType(ExecutableElement exec)
    {	
        switch (exec.getReturnType().getKind()) {
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
            return "void";
        case ARRAY:
        case DECLARED:  // A class or interface type.
            return exec.getReturnType().toString();
        case NONE:
        case NULL:
        case ERROR:
        case TYPEVAR:
        case WILDCARD:
        case PACKAGE:
        case EXECUTABLE:
        case OTHER:
        case UNION:         // TODO: What are union and intersection types?
        case INTERSECTION:
        default:
            throw new IllegalArgumentException();
        }
        
    }
    
    public static boolean hasVoidReturnType(ExecutableElement exec)
    {
        return exec.getReturnType().getKind() == TypeKind.VOID;
    }

    public static boolean hasPrimitiveReturnType(ExecutableElement exec)
    {
        return exec.getReturnType().getKind().isPrimitive();
    }

    public static boolean isFinalType(DeclaredType returnType)
    {
        return returnType.asElement().getModifiers().contains(Modifier.FINAL);
    }
    
    public static boolean isPrimitive(Element parameter)
    {
        return parameter.asType().getKind().isPrimitive();
    }
    
    public static boolean isArray(Element parameter)
    {
        return parameter.asType().getKind() == TypeKind.ARRAY;
    }

    /*
    public static void recurseTypes(Element el, Set<String> classes)
    {
        switch(el.asType().getKind())
        {
        case ARRAY:
        
        default:
            break;
            
        }
    }
    
    public static void recurseTypes(TypeMirror el, Set<String> classes)
    {
        
    }
    
    */ 
}
