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
package org.paninij.proc.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import org.paninij.proc.PaniniProcessor;


public class PaniniModel
{
    public static final String DEFAULT_MESSAGE_PACKAGE = "org.paninij.runtime.messages";
    public static final String DEFAULT_FUTURE_PACKAGE = "org.paninij.runtime.futures";
    public static final String DEFAULT_DUCK_PACKAGE = "org.paninij.runtime.ducks";

    public static final String CAPSULE_TEMPLATE_SUFFIX = "Template";
    public static final String CAPSULE_TEST_TEMPLATE_SUFFIX = "Template";
    public static final String CAPSULE_TEST_SUFFIX = "Test";
    public static final String SIGNATURE_TEMPLATE_SUFFIX = "Template";

    public static final String[] specialPaniniDecls = {"init", "design", "run"};

    public static boolean isPaniniCustom(TypeMirror returnType)
    {
        return returnType.toString().equals("org.paninij.lang.String");
    }

    public static boolean isProcedure(Element elem)
    {
        // TODO: decide on appropriate semantics for other cases.
        if (elem.getKind() == ElementKind.METHOD)
        {
            ExecutableElement method = (ExecutableElement) elem;

            if (isSpecialPaniniDecl(elem)) {
                return false;
            }

            Set<Modifier> modifiers = method.getModifiers();
            if (modifiers.contains(Modifier.STATIC) || modifiers.contains(Modifier.PRIVATE)) {
            	return false;
            }

            return true;
        }
        else
        {
            return false;
        }
    }

    public static boolean isSpecialPaniniDecl(Element elem)
    {
        if (elem.getKind() == ElementKind.METHOD)
        {
            String methodName = ((ExecutableElement) elem).getSimpleName().toString();
            for (String decl : specialPaniniDecls) {
                if (methodName.equals(decl)) {
                    return true;
                }
            }
            return false;
        }
        else
        {
            return false;
        }
    }

    /**
     * @return `true` if and only if the given capsule template describes an active capsule.
     *
     * Note: This method is (currently) equivalent to calling `hasRunDeclaration()`.
     *
     * Warning: This method *assumes* that `template` is a well-defined capsule template (i.e.
     * `template` passes all checks).
     */
    public static boolean isActive(TypeElement template)
    {
        return hasRunDeclaration(template);
    }

    /**
     * @return `true` if and only if the given capsule template has a `run()` declaration/method.
     *
     * Warning: This method *assumes* that `template` is a well-defined capsule template (i.e.
     * `template` passes all checks).
     */
    public static boolean hasRunDeclaration(TypeElement template)
    {
        List<ExecutableElement> methods = JavaModel.getMethodsNamed(template, "run");
        return methods.size() > 0;
    }

    /**
     * @return `true` if and only if the given capsule template has an `init()` declaration/method.
     *
     * Warning: This method *assumes* that `template` is a well-defined capsule template (i.e.
     * `template` passes all checks).
     */
    public static boolean hasInitDeclaration(TypeElement template)
    {
        List<ExecutableElement> methods = JavaModel.getMethodsNamed(template, "init");
        return methods.size() > 0;
    }

    /**
     * @return The name of the simple (i.e. unqualified) type of the given capsule template type.
     */
    public static String simpleTemplateName(TypeElement template) {
        return template.getSimpleName().toString();
    }

    /**
     * @return The name of the fully-qualified type of the given capsule template type.
     */
    public static String qualifiedTemplateName(TypeElement template) {
        return template.getQualifiedName().toString();
    }

    /**
     * @return The name of the simple (i.e. unqualified) capsule type associated with the given
     * capsule template type.
     *
     * Assumes that the given capsule template type is suffixed by `CAPSULE_TEMPLATE_SUFFIX`. This
     * is a useful helper method for dropping the `CAPSULE_TEMPLATE_SUFFIX`.
     */
    public static String simpleCapsuleName(TypeElement template)
    {
        // Drops the `CAPSULE_TEMPLATE_SUFFIX`.
        String name = template.getSimpleName().toString();
        assert(name.endsWith(CAPSULE_TEMPLATE_SUFFIX));
        return name.substring(0, name.length() - CAPSULE_TEMPLATE_SUFFIX.length());
    }


    /**
     * @return The name of the fully-qualified capsule type associated with the given capsule
     * template type.
     *
     * Assumes that the given capsule template type is suffixed by `CAPSULE_TEMPLATE_SUFFIX`. This
     * is a useful helper method for dropping the `CAPSULE_TEMPLATE_SUFFIX`.
     */
    public static String qualifiedCapsuleName(TypeElement template)
    {
         // Drops the `CAPSULE_TEMPLATE_SUFFIX`.
        String name = template.getQualifiedName().toString();
        assert(name.endsWith(CAPSULE_TEMPLATE_SUFFIX));
        return name.substring(0, name.length() - CAPSULE_TEMPLATE_SUFFIX.length());
    }
    
    
    public static String simpleSignatureName(TypeElement template) {
        // Drops the `SIGNATURE_TEMPLATE_SUFFIX`.
        String name = template.getSimpleName().toString();
        assert(name.endsWith(SIGNATURE_TEMPLATE_SUFFIX));
        return name.substring(0, name.length() - SIGNATURE_TEMPLATE_SUFFIX.length());
    }
    
    public static String qualifiedSignatureName(TypeElement template)
    {
         // Drops the `CAPSULE_TEMPLATE_SUFFIX`.
        String name = template.getQualifiedName().toString();
        assert(name.endsWith(SIGNATURE_TEMPLATE_SUFFIX));
        return name.substring(0, name.length() - SIGNATURE_TEMPLATE_SUFFIX.length());
    }

    
    /**
     * @return The name of the simple (i.e. unqualified) tester type associated with the given
     * capsule tester template type.
     *
     * Assumes that the given tester template type is suffixed by `CAPSULE_TESTER_SUFFIX`. This
     * is a useful helper method for dropping the `CAPSULE_TESTER_SUFFIX`.
     */
    public static String simpleTesterName(TypeElement tester)
    {
        // Drops the `CAPSULE_TEMPLATE_SUFFIX`.
        String name = tester.getSimpleName().toString();
        assert(name.endsWith(CAPSULE_TEST_TEMPLATE_SUFFIX));
        name = name.substring(0, name.length() - CAPSULE_TEST_TEMPLATE_SUFFIX.length());
        return name + CAPSULE_TEST_SUFFIX;
    }

    /**
     * @return The name of the fully-qualified tester type associated with the given tester
     * template type.
     *
     * Assumes that the given tester template type is suffixed by `CAPSULE_TESTER_SUFFIX`. This
     * is a useful helper method for dropping the `CAPSULE_TESTER_SUFFIX`.
     */
    public static String qualifiedTesterName(TypeElement tester)
    {
         // Drops the `CAPSULE_TEMPLATE_SUFFIX`.
        String name = tester.getQualifiedName().toString();
        assert(name.endsWith(CAPSULE_TEST_TEMPLATE_SUFFIX));
        name = name.substring(0, name.length() - CAPSULE_TEST_TEMPLATE_SUFFIX.length());
        return name + CAPSULE_TEST_SUFFIX;
    } 


    public static boolean isCapsuleFieldDecl(PaniniProcessor context, Element elem)
    {
        return isChildFieldDecl(context, elem) || isWiredFieldDecl(context, elem);
    }


    public static boolean hasCapsuleFieldDecls(PaniniProcessor context, TypeElement template)
    {
        return getCapsuleFieldDecls(context, template).isEmpty();
    }


    public static List<VariableElement> getCapsuleFieldDecls(PaniniProcessor context, TypeElement template)
    {
        List<VariableElement> fieldDecls = new ArrayList<VariableElement>();
        for (Element elem : template.getEnclosedElements())
        {
            if (isCapsuleFieldDecl(context, elem)) {
                fieldDecls.add((VariableElement) elem);
            }
        }
        return fieldDecls;
    }


    public static boolean isChildFieldDecl(PaniniProcessor context, Element elem)
    {
        return elem.getKind() == ElementKind.FIELD
            && JavaModel.isAnnotatedBy(context, elem, "org.paninij.lang.Child");
    }


    public static boolean hasChildFieldDecls(PaniniProcessor context, TypeElement template) {
        return getWiredFieldDecls(context, template).isEmpty() == false;
    }


    public static List<VariableElement> getChildFieldDecls(PaniniProcessor context,
                                                           TypeElement template)
    {
        List<VariableElement> children = new ArrayList<VariableElement>();
        for (Element elem : template.getEnclosedElements())
        {
            if (isChildFieldDecl(context, elem)) {
                children.add((VariableElement) elem);
            }
        }
        return children;
    }


    public static boolean isWiredFieldDecl(PaniniProcessor context, Element elem)
    {
        return elem.getKind() == ElementKind.FIELD
            && JavaModel.isAnnotatedBy(context, elem, "org.paninij.lang.Wired");
    }


    public static boolean hasWiredFieldDecls(PaniniProcessor context, TypeElement template) {
        return getWiredFieldDecls(context, template).isEmpty() == false;
    }

    public static List<VariableElement> getWiredFieldDecls(PaniniProcessor context, TypeElement template)
    {
        List<VariableElement> wired = new ArrayList<VariableElement>();
        for (Element elem : template.getEnclosedElements())
        {
            if (isWiredFieldDecl(context, elem)) {
                wired.add((VariableElement) elem);
            }
        }
        return wired;
    }
    
    public static boolean isStateFieldDecl(PaniniProcessor context, Element elem)
    {
        return elem.getKind() == ElementKind.FIELD
            && isWiredFieldDecl(context, elem) == false
            && isChildFieldDecl(context, elem) == false;
        
    }
    
    public static boolean hasStateFieldDecl(PaniniProcessor context, TypeElement template)
    {
        return getStateFieldDecls(context, template).isEmpty() == false;
    }

    public static List<VariableElement> getStateFieldDecls(PaniniProcessor context, TypeElement template)
    {
        List<VariableElement> state_decls = new ArrayList<VariableElement>();
        for (Element elem : template.getEnclosedElements())
        {
            if (isStateFieldDecl(context, elem)) {
                state_decls.add((VariableElement) elem);
            }
            
        }
        return state_decls;
    }

    /**
     * A capsule is a "root" capsule if and only if it is active and has no `@Wired` fields.
     */
    public static boolean isRootCapsule(PaniniProcessor context, TypeElement template)
    {
        return hasWiredFieldDecls(context, template) == false && isActive(template);
    }


    /**
     * Returns `true` if and only if the given capsule template has a design declaration.
     */
    public static boolean hasCapsuleDesignDecl(TypeElement template)
    {
        return getCapsuleDesignDecl(template) != null;
    }


    /**
     * Returns the `ExecutableElement` representing the given capsule template design declaration,
     * or returns `null` if there is no such declaration.
     *
     * Warning: This method *assumes* that `template` is a well-defined capsule template (i.e.
     * `template` passes all checks).
     */
    public static ExecutableElement getCapsuleDesignDecl(TypeElement template)
    {
        List<ExecutableElement> decls = JavaModel.getMethodsNamed(template, "design");
        if (decls.isEmpty()) {
            return null;
        } else {
            return decls.get(0);
        }
    }


    /**
     * Returns a list of all of the procedures (represented as `ExcecutableElement`s) defined on the
     * given `template`.
     */
    public static List<ExecutableElement> getProcedures(TypeElement template)
    {
        List<ExecutableElement> rv = new ArrayList<ExecutableElement>();
        for (Element elem : template.getEnclosedElements())
        {
            if (isProcedure(elem)) {
                rv.add((ExecutableElement) elem);
            }
        }
        return rv;
    }


    /**
     * Inspects the given capsule template, finds the design declaration on it, then returns a
     * String representation of a `wire()` method declaration.
     *
     * For example, if a user-defined capsule template has the form
     *
     *     ```
     *     @Capsule
     *     public class BazTemplate
     *     {
     *         @Wired Foo foo;
     *         @Wired Bar bar;
     *         // ...
     *     }
     *     ```
     *
     * where `foo` and `bar` are the only `@Wired`-annotated fields on the template, then this
     * method would return the `String`
     *
     *     "public void wire(Foo foo, Bar bar)"
     *
     * Note: If the `template` has no wired capsules, then this method returns `null`.
     */
    public static String buildWireMethodDecl(PaniniProcessor context, TypeElement template)
    {
        List<String> paramDecls = new ArrayList<String>();
        for (VariableElement varElem : getWiredFieldDecls(context, template)) {
            paramDecls.add(Source.buildVariableDecl(varElem));
        }

        if (paramDecls.isEmpty()) {
            return null;
        } else {
            return Source.format("public void wire(#0)", String.join(", ", paramDecls));
        }
    }
}