package org.paninij.apt.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import org.paninij.apt.PaniniPress;
import org.paninij.lang.Capsule;
import org.paninij.lang.CapsuleInterface;
import org.paninij.lang.Signature;

public class PaniniModelInfo
{
    public static final String CAPSULE_TEMPLATE_SUFFIX = "Template";
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
        List<ExecutableElement> methods = JavaModelInfo.getMethodsNamed(template, "run");
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
        List<ExecutableElement> methods = JavaModelInfo.getMethodsNamed(template, "init");
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


    public static boolean isCapsuleFieldDecl(PaniniPress context, Element elem)
    {
        return isChildFieldDecl(context, elem) || isWiredFieldDecl(context, elem);
    }

    
    public static boolean hasCapsuleFieldDecls(PaniniPress context, TypeElement template)
    {
        return getCapsuleFieldDecls(context, template).isEmpty();
    }


    public static List<VariableElement> getCapsuleFieldDecls(PaniniPress context, TypeElement template)
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


    public static boolean isChildFieldDecl(PaniniPress context, Element elem)
    {
        return elem.getKind() == ElementKind.FIELD
            && JavaModelInfo.isAnnotatedBy(context, elem, "org.paninij.lang.Child");
    }


    public static boolean hasChildFieldDecls(PaniniPress context, TypeElement template) {
        return getWiredFieldDecls(context, template).isEmpty() == false;
    }

 
    public static List<VariableElement> getChildFieldDecls(PaniniPress context,
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

  
    public static boolean isWiredFieldDecl(PaniniPress context, Element elem)
    {
        return elem.getKind() == ElementKind.FIELD
            && JavaModelInfo.isAnnotatedBy(context, elem, "org.paninij.lang.Wired");
    }


    public static boolean hasWiredFieldDecls(PaniniPress context, TypeElement template) {
        return getWiredFieldDecls(context, template).isEmpty() == false;
    }
    
    public static List<VariableElement> getWiredFieldDecls(PaniniPress context, TypeElement template)
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


    /**
     * A capsule is a "root" capsule if and only if it is active and has no `@Wired` fields.
     */
    public static boolean isRootCapsule(PaniniPress context, TypeElement template)
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
        List<ExecutableElement> decls = JavaModelInfo.getMethodsNamed(template, "design");
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
     * Returns the set of all `DuckShape`s which the given capsule template will use.
     */
    public static Set<DuckShape> getDuckShapes(TypeElement template)
    {
        Set<DuckShape> rv = new HashSet<DuckShape>();
        for (ExecutableElement proc : getProcedures(template)) {
            rv.add(new DuckShape(proc));
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
    public static String buildWireMethodDecl(PaniniPress context, TypeElement template)
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