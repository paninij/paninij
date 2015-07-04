package org.paninij.soter;

import java.util.List;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;

import static org.paninij.soter.JavaModel.*;

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
     * @param method An arbitrary method on a template class annotated with @Capsule.
     */
    public static boolean isSpecialCapsuleDecl(IMethod method)
    {
        return isNamed(method, "init")
            || isNamed(method, "design")
            || isNamed(method, "run");
    }
    

    /**
     * @param method An arbitrary method on a capsule interface annotated with @CapsuleInterface.
     */
    public static boolean isCapsuleProcedure(IMethod method)
    {
        // TODO: Everything
        throw new UnsupportedOperationException("TODO");
    }
    

    /**
     * @param method An arbitrary method on a template class annotated with @Capsule.
     */
    public static boolean isTemplateProcedure(IMethod method)
    {
        return method.isPublic() == true  // A only a capsule's public methods are procedures.
            && isSpecialCapsuleDecl(method) == false;  // Don't count special decls as procedures.
    }
    
    
    /**
     * @param template A capsule template class annotated with @Capsule.
     * @return The template's run declaration, if it defines an active capsule. Otherwise `null`.
     */
    public static IMethod getRunDecl(IClass template)
    {
        return getApplicationMethods(template)
                 .stream()
                 .filter(m -> isNamed(m, "run"))
                 .findFirst()
                 .orElse(null);
    }
 

    /**
     * @param template A capsule template class annotated with @Capsule.
     */
    public static List<IMethod> getTemplateProcedures(IClass template)
    {
        return getApplicationMethods(template)
                 .stream()
                 .filter(m -> isTemplateProcedure(m))
                 .collect(toList());
    }
    
   
    /**
     * @param template A capsule template class annotated with @Capsule.
     * @return A list of all of the "application" (i.e. not "primordial") methods on the template.
     */
    private static List<IMethod> getApplicationMethods(IClass template)
    {
        // TODO: Make this less brittle.
        String prefix = "< Application,";
        Predicate<IMethod> isRelevant = (m -> m.toString().startsWith(prefix));

        return template.getAllMethods()
                       .stream()
                       .filter(isRelevant)
                       .collect(toList());
    }
}
