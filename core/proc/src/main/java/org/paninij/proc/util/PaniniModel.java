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

package org.paninij.proc.util;

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


public class PaniniModel
{
    public static final String DEFAULT_MESSAGE_PACKAGE = "org.paninij.runtime.messages";
    public static final String DEFAULT_FUTURE_PACKAGE = "org.paninij.runtime.futures";
    public static final String DEFAULT_DUCK_PACKAGE = "org.paninij.runtime.ducks";

    public static final String CAPSULE_CORE_SUFFIX = "Core";
    public static final String CAPSULE_TEST_CORE_SUFFIX = "Core";
    public static final String CAPSULE_TEST_SUFFIX = "Test";
    public static final String SIGNATURE_SPEC_SUFFIX = "Spec";

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
     * @return `true` if and only if the given capsule core describes an active capsule.
     *
     * Note: This method is (currently) equivalent to calling `hasRunDeclaration()`.
     *
     * Warning: This method *assumes* that `core` is a well-defined capsule core (i.e.
     * `core` passes all checks).
     */
    public static boolean isActive(TypeElement core)
    {
        return hasRunDeclaration(core);
    }

    /**
     * @return `true` if and only if the given capsule core has a `run()` declaration/method.
     *
     * Warning: This method *assumes* that `core` is a well-defined capsule core (i.e.
     * `core` passes all checks).
     */
    public static boolean hasRunDeclaration(TypeElement core)
    {
        List<ExecutableElement> methods = JavaModel.getMethodsNamed(core, "run");
        return methods.size() > 0;
    }

    /**
     * @return `true` if and only if the given capsule core has an `init()` declaration/method.
     *
     * Warning: This method *assumes* that `core` is a well-defined capsule core (i.e.
     * `core` passes all checks).
     */
    public static boolean hasInitDeclaration(TypeElement core)
    {
        List<ExecutableElement> methods = JavaModel.getMethodsNamed(core, "init");
        return methods.size() > 0;
    }

    /**
     * @return The name of the simple (i.e. unqualified) type of the given capsule core type.
     */
    public static String simpleCapsuleCoreName(TypeElement core) {
        return core.getSimpleName().toString();
    }

    /**
     * @return The name of the fully-qualified type of the given capsule core type.
     */
    public static String qualifiedCapsuleCoreName(TypeElement core) {
        return core.getQualifiedName().toString();
    }

    /**
     * @return The name of the simple (i.e. unqualified) capsule type associated with the given
     * capsule core type.
     *
     * Assumes that the given capsule core type is suffixed by `CAPSULE_CORE_SUFFIX`. This
     * is a useful helper method for dropping the `CAPSULE_CORE_SUFFIX`.
     */
    public static String simpleCapsuleName(TypeElement core)
    {
        // Drops the `CAPSULE_CORE_SUFFIX`.
        String name = core.getSimpleName().toString();
        assert(name.endsWith(CAPSULE_CORE_SUFFIX));
        return name.substring(0, name.length() - CAPSULE_CORE_SUFFIX.length());
    }


    /**
     * @return The name of the fully-qualified capsule type associated with the given capsule
     * core type.
     *
     * Assumes that the given capsule core type is suffixed by `CAPSULE_CORE_SUFFIX`. This
     * is a useful helper method for dropping the `CAPSULE_CORE_SUFFIX`.
     */
    public static String qualifiedCapsuleName(TypeElement core)
    {
         // Drops the `CAPSULE_CORE_SUFFIX`.
        String name = core.getQualifiedName().toString();
        assert(name.endsWith(CAPSULE_CORE_SUFFIX));
        return name.substring(0, name.length() - CAPSULE_CORE_SUFFIX.length());
    }
    
    
    public static String simpleSignatureName(TypeElement core) {
        // Drops the `SIGNATURE_SPEC_SUFFIX`.
        String name = core.getSimpleName().toString();
        assert(name.endsWith(SIGNATURE_SPEC_SUFFIX));
        return name.substring(0, name.length() - SIGNATURE_SPEC_SUFFIX.length());
    }
    
    public static String qualifiedSignatureName(TypeElement core)
    {
         // Drops the `CAPSULE_CORE_SUFFIX`.
        String name = core.getQualifiedName().toString();
        assert(name.endsWith(SIGNATURE_SPEC_SUFFIX));
        return name.substring(0, name.length() - SIGNATURE_SPEC_SUFFIX.length());
    }

    
    /**
     * @return The name of the simple (i.e. unqualified) tester type associated with the given
     * capsule tester core type.
     *
     * Assumes that the given tester core type is suffixed by `CAPSULE_TESTER_SUFFIX`. This
     * is a useful helper method for dropping the `CAPSULE_TESTER_SUFFIX`.
     */
    public static String simpleTesterName(TypeElement tester)
    {
        // Drops the `CAPSULE_CORE_SUFFIX`.
        String name = tester.getSimpleName().toString();
        assert(name.endsWith(CAPSULE_TEST_CORE_SUFFIX));
        name = name.substring(0, name.length() - CAPSULE_TEST_CORE_SUFFIX.length());
        return name + CAPSULE_TEST_SUFFIX;
    }

    /**
     * @return The name of the fully-qualified tester type associated with the given tester
     * core type.
     *
     * Assumes that the given tester core type is suffixed by `CAPSULE_TESTER_SUFFIX`. This
     * is a useful helper method for dropping the `CAPSULE_TESTER_SUFFIX`.
     */
    public static String qualifiedTesterName(TypeElement tester)
    {
         // Drops the `CAPSULE_CORE_SUFFIX`.
        String name = tester.getQualifiedName().toString();
        assert(name.endsWith(CAPSULE_TEST_CORE_SUFFIX));
        name = name.substring(0, name.length() - CAPSULE_TEST_CORE_SUFFIX.length());
        return name + CAPSULE_TEST_SUFFIX;
    } 


    public static boolean isCapsuleFieldDecl(ProcessingEnvironment context, Element elem)
    {
        return isLocalFieldDecl(context, elem) || isImportFieldDecl(context, elem);
    }


    public static boolean hasCapsuleFieldDecls(ProcessingEnvironment context, TypeElement core)
    {
        return getCapsuleFieldDecls(context, core).isEmpty();
    }


    public static List<VariableElement> getCapsuleFieldDecls(ProcessingEnvironment context, TypeElement core)
    {
        List<VariableElement> fieldDecls = new ArrayList<>();
        for (Element elem : core.getEnclosedElements())
        {
            if (isCapsuleFieldDecl(context, elem)) {
                fieldDecls.add((VariableElement) elem);
            }
        }
        return fieldDecls;
    }


    public static boolean isLocalFieldDecl(ProcessingEnvironment context, Element elem)
    {
        return elem.getKind() == ElementKind.FIELD
            && JavaModel.isAnnotatedBy(context, elem, "org.paninij.lang.Local");
    }


    public static boolean hasLocalFieldDecls(ProcessingEnvironment context, TypeElement core) {
        return ! getImportFieldDecls(context, core).isEmpty();
    }


    public static List<VariableElement> getLocalFieldDecls(ProcessingEnvironment context,
                                                           TypeElement core)
    {
        List<VariableElement> locals = new ArrayList<>();
        for (Element elem : core.getEnclosedElements())
        {
            if (isLocalFieldDecl(context, elem)) {
                locals.add((VariableElement) elem);
            }
        }
        return locals;
    }


    public static boolean isImportFieldDecl(ProcessingEnvironment context, Element elem)
    {
        return elem.getKind() == ElementKind.FIELD
            && JavaModel.isAnnotatedBy(context, elem, "org.paninij.lang.Import");
    }


    public static boolean hasImportFieldDecls(ProcessingEnvironment context, TypeElement core) {
        return ! getImportFieldDecls(context, core).isEmpty();
    }

    public static List<VariableElement> getImportFieldDecls(ProcessingEnvironment context, TypeElement core)
    {
        List<VariableElement> importFields = new ArrayList<VariableElement>();
        for (Element elem : core.getEnclosedElements())
        {
            if (isImportFieldDecl(context, elem)) {
                importFields.add((VariableElement) elem);
            }
        }
        return importFields;
    }
    
    public static boolean isStateFieldDecl(ProcessingEnvironment context, Element elem)
    {
        return elem.getKind() == ElementKind.FIELD
            && ! isImportFieldDecl(context, elem)
            && ! isLocalFieldDecl(context, elem);
        
    }
    
    public static boolean hasStateFieldDecl(ProcessingEnvironment context, TypeElement core)
    {
        return ! getStateFieldDecls(context, core).isEmpty();
    }

    public static List<VariableElement> getStateFieldDecls(ProcessingEnvironment context, TypeElement core)
    {
        List<VariableElement> state_decls = new ArrayList<>();
        for (Element elem : core.getEnclosedElements())
        {
            if (isStateFieldDecl(context, elem)) {
                state_decls.add((VariableElement) elem);
            }
            
        }
        return state_decls;
    }

    /**
     * A capsule is a "root" capsule if and only if it is active and has no `@Import` fields.
     */
    public static boolean isRootCapsule(ProcessingEnvironment context, TypeElement core)
    {
        return hasImportFieldDecls(context, core) == false && isActive(core);
    }


    /**
     * Returns `true` if and only if the given capsule core has a design declaration.
     */
    public static boolean hasCapsuleDesignDecl(TypeElement core)
    {
        return getCapsuleDesignDecl(core) != null;
    }


    /**
     * Returns the `ExecutableElement` representing the given capsule core design declaration,
     * or returns `null` if there is no such declaration.
     *
     * Warning: This method *assumes* that `core` is a well-defined capsule core (i.e.
     * `core` passes all checks).
     */
    public static ExecutableElement getCapsuleDesignDecl(TypeElement core)
    {
        List<ExecutableElement> decls = JavaModel.getMethodsNamed(core, "design");
        if (decls.isEmpty()) {
            return null;
        } else {
            return decls.get(0);
        }
    }


    /**
     * Returns a list of all of the procedures (represented as `ExcecutableElement`s) defined on the
     * given `core`.
     */
    public static List<ExecutableElement> getProcedures(TypeElement core)
    {
        List<ExecutableElement> rv = new ArrayList<>();
        for (Element elem : core.getEnclosedElements())
        {
            if (isProcedure(elem)) {
                rv.add((ExecutableElement) elem);
            }
        }
        return rv;
    }


    /**
     * Inspects the given capsule core, finds the design declaration on it, then returns a
     * String representation of a `imports()` method declaration.
     *
     * <p>For example, if a user-defined capsule core has the form
     *
     * <pre><code>
     * &#64;Capsule
     * public class BazCore {
     *     &#64;Import Foo foo;
     *     &#64;Import Bar bar;
     *     // ...
     * }
     * </code></pre>
     *
     * <p>where `foo` and `bar` are the only `@Import`-annotated fields on the core, then this
     * method would return the `String`
     *
     * <pre><code>
     * public void imports(Foo foo, Bar bar)
     * </code></pre>
     *
     * <p>Note: If the `core` has no `@Import` capsules, then this method returns `null`.
     */
    public static String buildExportMethodDecl(ProcessingEnvironment context, TypeElement core)
    {
        List<String> paramDecls = new ArrayList<>();
        for (VariableElement varElem : getImportFieldDecls(context, core)) {
            paramDecls.add(Source.buildVariableDecl(varElem));
        }

        if (paramDecls.isEmpty()) {
            return null;
        } else {
            return Source.format("public void imports(#0)", String.join(", ", paramDecls));
        }
    }
}
