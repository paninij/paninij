package org.paninij.soter.util;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IField;
import com.ibm.wala.classLoader.IMethod;

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
     * @param method An arbitrary method on a capsule interface annotated with `@CapsuleInterface`.
     */
    public static boolean isCapsuleProcedure(IMethod method)
    {
        assert isCapsuleInterface(method.getDeclaringClass());
        // TODO: Everything
        throw new UnsupportedOperationException("TODO");
    }
    

    /**
     * @param method An arbitrary method on a template class annotated with `@Capsule`.
     */
    public static boolean isTemplateProcedure(IMethod method)
    {
        assert isCapsuleTemplate(method.getDeclaringClass());
        return method.isPublic() == true  // A only a capsule's public methods are procedures.
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
     * @return Stream of the template's fields which are annotated with `@Child`.
     */
    public static Stream<IField> getChildCapsuleDecls(IClass template)
    {
        assert isCapsuleTemplate(template);
        return template.getAllFields()
                       .stream()
                       .filter(a -> hasAnnotationNamed(a, "Child"));
    }
    

    /**
     * @param template A capsule template class annotated with `@Capsule`.
     * @return Stream of the template's fields which are annotated with `@Wired`.
     */
    public static Stream<IField> getWiredCapsuleDecls(IClass template)
    {
        assert isCapsuleTemplate(template);
        return template.getAllFields()
                       .stream()
                       .filter(a -> hasAnnotationNamed(a, "Wired"));
    }
    
    
    /**
     * @param template A capsule template class annotated with `@Capsule`.
     * @return Stream of the template's fields which are states (i.e. neither `@Child` or `@Wired`).
     */
    public static Stream<IField> getStateDecls(IClass template)
    {
        assert isCapsuleTemplate(template);
        return template.getAllFields()
                       .stream()
                       .filter(a -> hasAnnotationNamed(a, "Child") == false
                                 && hasAnnotationNamed(a, "Wired") == false);
    }
 
    
    /**
     * @param template A capsule template class annotated with `@Capsule`.
     */
    public static Stream<IMethod> getProcedures(IClass template)
    {
        assert isCapsuleTemplate(template);
        return JavaModel.getApplicationMethodsList(template)
                 .stream()
                 .filter(m -> isTemplateProcedure(m));
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
}
