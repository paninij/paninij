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
package org.paninij.soter.util;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IField;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.MethodReference;
import com.ibm.wala.types.TypeName;
import com.ibm.wala.types.TypeReference;

import static org.paninij.soter.util.JavaModel.*;

/**
 * Note that all helper methods here assume that any given @PaniniJ artifacts (e.g. capsules,
 * capsule templates, capsule interfaces, etc.) are well-formed, that is, they were constructed
 * by @PaniniJ and passed all correctness checks. In particular, if an artifact has an @PaniniJ
 * annotation, then it is assumed to be well-formed.
 */
public class PaniniModel
{
    // TODO: Do annotation checking in a more robust way.
    private static final String CAPSULE_ANNOTATION_NAME = "Capsule";
    private static final String CAPSULE_INTERFACE_ANNOTATION_NAME = "CapsuleInterface";
    private static final String CAPSULE_MOCKUP_SUFFIX = "$Mockup";


    /**
     * @param clazz An arbitrary class.
     */
    public static boolean isCapsuleTemplate(IClass clazz)
    {
        return hasAnnotationNamed(clazz, CAPSULE_ANNOTATION_NAME);
    }
    
    
    /**
     * @param clazz An arbitrary class.
     */
    public static boolean isCapsuleInterface(IClass clazz)
    {
        return hasAnnotationNamed(clazz, CAPSULE_INTERFACE_ANNOTATION_NAME);
    }
    
    
    /**
     * @param method An arbitrary method on a template class annotated with `@Capsule`.
     */
    public static boolean isSpecialCapsuleDecl(IMethod method)
    {
        assert isCapsuleTemplate(method.getDeclaringClass());
        return isNamed(method, "init")
            || isNamed(method, "design")
            || isNamed(method, "run");
    }
    

    /**
     * @param method An arbitrary method declared on a template class annotated with `@Capsule`.
     */
    public static boolean isProcedure(IMethod method)
    {
        assert isCapsuleTemplate(method.getDeclaringClass());
        return method.isPublic() == true  // Only a capsule's public methods are procedures.
            && isSpecialCapsuleDecl(method) == false;  // Don't count special decls as procedures.
    }
    
    

    /**
     * @param template A capsule template class annotated with `@Capsule`.
     * @return The template's run declaration, if it defines an active capsule. Otherwise `null`.
     */
    public static IMethod getRunDecl(IClass template)
    {
        assert isCapsuleTemplate(template);
        return JavaModel.getApplicationMethodsList(template)
                 .stream()
                 .filter(m -> isNamed(m, "run"))
                 .findFirst()
                 .orElse(null);
    }
    

    /**
     * @param template A capsule template class annotated with `@Capsule`.
     * @return The template's `init()` declaration, if it has one. Otherwise `null`.
     */
    public static IMethod getInitDecl(IClass template)
    {
        assert isCapsuleTemplate(template);
        return JavaModel.getApplicationMethodsList(template)
                 .stream()
                 .filter(m -> isNamed(m, "init"))
                 .findFirst()
                 .orElse(null);
    }
    
    
    /**
     * @param template A capsule template class annotated with `@Capsule`.
     * @return Stream of the template's fields which are annotated with `@Local`.
     */
    public static Stream<IField> getLocalDecls(IClass template)
    {
        assert isCapsuleTemplate(template);
        return template.getAllFields()
                       .stream()
                       .filter(a -> hasAnnotationNamed(a, "Local"));
    }
    

    /**
     * @param template A capsule template class annotated with `@Capsule`.
     * @return Stream of the template's fields which are annotated with `@Import`.
     */
    public static Stream<IField> getImportDecls(IClass template)
    {
        assert isCapsuleTemplate(template);
        return template.getAllFields()
                       .stream()
                       .filter(a -> hasAnnotationNamed(a, "Import"));
    }
    
    
    /**
     * @param template A capsule template class annotated with `@Capsule`.
     * @return Stream of the template's fields that are states (i.e. neither `@Local` or `@Import`).
     */
    public static Stream<IField> getStateDecls(IClass template)
    {
        assert isCapsuleTemplate(template);
        return template.getAllFields()
                       .stream()
                       .filter(a -> hasAnnotationNamed(a, "Local") == false
                                 && hasAnnotationNamed(a, "Import") == false);
    }
 
    
    /**
     * @param template A capsule template class annotated with `@Capsule`.
     */
    public static Stream<IMethod> getProcedures(IClass template)
    {
        assert isCapsuleTemplate(template);
        return JavaModel.getApplicationMethodsList(template)
                 .stream()
                 .filter(m -> isProcedure(m));
    }


    /**
     * TODO: Should be deprecated in favor of using `getProcedures()` directly.
     * 
     * @param template A capsule template class annotated with `@Capsule`.
     */
    public static List<IMethod> getProceduresList(IClass template)
    {
        assert isCapsuleTemplate(template);
        return getProcedures(template).collect(toList());
    }
    
    
    public static TypeReference getCapsuleMockupClassReference(TypeReference capsuleInterface)
    {
        ClassLoaderReference loader = capsuleInterface.getClassLoader();

        TypeName interfaceName = capsuleInterface.getName();
        String pkg = interfaceName.getPackage().toString();
        String name = interfaceName.getClassName().toString() + CAPSULE_MOCKUP_SUFFIX;

        return TypeReference.findOrCreateClass(loader, pkg, name);
    }
    
    public static boolean isRemoteProcedure(MethodReference method, IClassHierarchy cha)
    {
        return isCapsuleInterface(cha.lookupClass(method.getDeclaringClass()));
    }

    public static boolean isRemoteProcedure(IMethod method)
    {
        return isCapsuleInterface(method.getDeclaringClass());
    }
    
    public static boolean isKnownSafeTypeForTransfer(TypeReference typeRef)
    {
        // TODO: Add String and primitive box types (e.g. Integer).
        return typeRef.isPrimitiveType();
    }

}
