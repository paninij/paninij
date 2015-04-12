package org.paninij.apt.util;

import java.util.ArrayList;
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

    public static boolean needsProcedureWrapper(Element elem)
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

    public static boolean isCapsuleFieldDecl(PaniniPress context, VariableElement fieldDecl)
    {
        TypeMirror fieldType = fieldDecl.asType();
                
        return JavaModelInfo.isAnnotatedBy(context, fieldType, "org.paninij.lang.CapsuleInterface")
            || JavaModelInfo.isAnnotatedBy(context, fieldType, "org.paninij.lang.Signature")
            || JavaModelInfo.isAnnotatedBy(context, fieldDecl, "org.paninij.lang.Child")
            || JavaModelInfo.isAnnotatedBy(context, fieldDecl, "org.paninij.lang.Wired");
    }

    public static List<VariableElement> getCapsuleFieldDecls(PaniniPress context, TypeElement template)
    {
        List<VariableElement> decls = new ArrayList<VariableElement>();
        for (Element e : template.getEnclosedElements())
        {
            if (e.getKind() == ElementKind.FIELD)
            {
                VariableElement varElem = (VariableElement) e;
                if (isCapsuleFieldDecl(context, varElem)) {
                    decls.add(varElem);
                }
            }
        }
        return decls;
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
     * Returns `true` if and only if the given capsule template has a design declaration.
     */
    public static boolean hasCapsuleDesignDecl(TypeElement template)
    {
        return getCapsuleDesignDecl(template) != null;
    }

    public static List<VariableElement> getCapsuleRequirements(PaniniPress context,
                                                               TypeElement template)
    {
        ArrayList<VariableElement> reqs = new ArrayList<VariableElement>();
        ExecutableElement design = getCapsuleDesignDecl(template);

        if (design == null) return reqs;

        List<? extends VariableElement> params = design.getParameters();
        List<VariableElement> decls = getCapsuleFieldDecls(context, template);

        for (VariableElement d : decls) {
            for (VariableElement p : params) {
                if (d.toString().equals(p.toString())) {
                    reqs.add(d);
                }
            }
        }

        return reqs;
    }
    
    public static boolean hasCapsuleRequirements(PaniniPress context, TypeElement template)
    {
        return getCapsuleRequirements(context, template).isEmpty() == false;
    }

    public static List<VariableElement> getCapsuleChildren(PaniniPress context,
                                                           TypeElement template)
    {
        List<VariableElement> decls = getCapsuleFieldDecls(context, template);
        ExecutableElement design = getCapsuleDesignDecl(template);

        if (design == null) return decls;

        List<? extends VariableElement> params = design.getParameters();
        ArrayList<VariableElement> children = new ArrayList<VariableElement>();

        boolean found;
        for (VariableElement d : decls) {
            found = false;
            for (VariableElement p : params) {
                if (d.toString().equals(p.toString())) {
                    found = true;
                    break;
                }
            }
            if (!found) children.add(d);
        }

        return children;
    }

    /**
     * Inspects the given capsule template, finds the design declaration on it, then returns a
     * String representation of a `wire()` method declaration.
     *
     * For example, if a user-defined capsule template has the design declaration of the form
     *
     *     void design(Self s, Foo foo, Bar bar) {
     *         // ...
     *     }
     *
     * then this method would return the `String`
     *
     *     "public void wire(Foo foo, Bar bar)"
     *
     * Note that if `template` has no design declaration, then this method returns `null`.
     */
    public static String buildCapsuleWireMethodDecl(TypeElement template)
    {
        ExecutableElement designDecl = getCapsuleDesignDecl(template);
        if (designDecl == null) {
            return null;
        }

        List<? extends VariableElement> varElems = designDecl.getParameters();
        List<String> paramStrings = new ArrayList<String>(varElems.size());

        // For each element in `varElems`, aside from the first `self` element, convert that
        // element into a String, and add it to `paramStrings`.
        for (int idx = 1; idx < varElems.size(); idx++)
        {
            VariableElement varElem = varElems.get(idx);
            paramStrings.add(Source.buildVariableDecl(varElem));
        }

        return Source.format("public void wire(#0)", String.join(", ", paramStrings));
    }
}