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
 * Contributor(s): Dalton Mills, David Johnston
 */
package org.paninij.apt.util;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.paninij.apt.PaniniProcessor;


public class JavaModelInfo {

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
            return "Void";
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

    public static TypeElement getTypeElement(PaniniProcessor context, String className) {
        return context.getElementUtils().getTypeElement(className);
    }

    /**
     * @return A list of methods on the given `typeElem` whose name matches the given `name`; the
     * returned `ExecutableElement`s are listed in the same order in which the they are defined
     * within the given `typeElem`.
     */
    public static List<ExecutableElement> getMethodsNamed(TypeElement typeElem, String name)
    {
        List<ExecutableElement> rv = new ArrayList<ExecutableElement>();
        for (Element enclosedElem : typeElem.getEnclosedElements())
        {
            if (enclosedElem.getKind() == ElementKind.METHOD && isNamed(enclosedElem, name)) {
                rv.add((ExecutableElement) enclosedElem);
            }
        }
        return rv;
    }

    /**
     * @return `true` if and only if the given executable element has the given name.
     */
    public static boolean isNamed(Element elem, String name)
    {
        return elem.getSimpleName().toString().equals(name);
    }

    public static boolean hasVoidReturnType(ExecutableElement exec)
    {
        return exec.getReturnType().getKind() == TypeKind.VOID;
    }

    public static boolean isFinalType(TypeMirror returnType)
    {
        if (returnType.getKind() != TypeKind.DECLARED)
        {
            return false;
        }
        else
        {
            Element elem = ((DeclaredType) returnType).asElement();
            return isFinalType((TypeElement) elem);
        }
    }

    public static boolean isFinalType(TypeElement type)
    {
        return type.getModifiers().contains(Modifier.FINAL);
    }

    public static boolean isPrimitive(TypeMirror type)
    {
        return type.getKind().isPrimitive();
    }

    public static boolean isPrimitive(Element type)
    {
        return isPrimitive(type.asType());
    }

    public static boolean hasPrimitiveReturnType(ExecutableElement exec)
    {
        return isPrimitive(exec.getReturnType());
    }

    public static boolean isArray(TypeMirror type)
    {
        return type.getKind() == TypeKind.ARRAY;
    }

    public static boolean isArray(Element type)
    {
        return isArray(type.asType());
    }

    /*
    // Commented out to instead use the version `PaniniPress`-based version.
    public static <A extends Annotation> boolean isAnnotatedBy(Class<A> annotationType, Element elem) {
        // FIXME
        return elem.getAnnotation(annotationType) != null;
//        return !elem.asType().getAnnotationMirrors().isEmpty();
//        return elem.asType().getAnnotationsByType(annotationType).length > 0;
//        return !elem.getAnnotationMirrors().isEmpty();
//        return elem.getAnnotation(annotationType) != null;
    }
    */

    public static <A extends Annotation> boolean isAnnotatedBy(PaniniProcessor context,
                                                               TypeMirror typeMirror,
                                                               String annotationName)
    {
        TypeElement typeElem = (TypeElement) context.getTypeUtils().asElement(typeMirror);
        return isAnnotatedBy(context, typeElem, annotationName);
    }

    public static <A extends Annotation> boolean isAnnotatedBy(PaniniProcessor context,
                                                               Element elem,
                                                               String annotationName)
    {
        TypeElement annotationType = getTypeElement(context, annotationName);
        if (annotationType == null)
        {
            String msg = "Called `isAnnotatedBy()` with an `annotationName` which could not be found.";
            throw new IllegalArgumentException(msg);
        }

        for (AnnotationMirror am : elem.getAnnotationMirrors())
        {
             if (context.getTypeUtils().isSameType(am.getAnnotationType(), annotationType.asType()))
             {
                 return true;
             }
        }
        return false;
    }


    public static String getPackage(TypeMirror type)
    {
        String qualified = type.toString();
        int idx = qualified.lastIndexOf('.');
        return idx == -1 ? "" : qualified.substring(0,  idx);
    }
}
