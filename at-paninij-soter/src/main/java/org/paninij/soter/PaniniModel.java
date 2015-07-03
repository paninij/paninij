package org.paninij.soter;

import java.util.function.Predicate;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.types.MethodReference;
import com.ibm.wala.types.TypeReference;
import com.ibm.wala.types.annotations.Annotation;

import static org.paninij.soter.JavaModel.*;

public class PaniniModel
{
    private static final String CAPSULE_ANNOTATION_NAME = "Capsule";
    private static final String CAPSULE_TEMPLATE_SUFFIX = "Template";
    
    
    public static boolean isCapsuleTemplate(TypeReference type)
    {
        throw new UnsupportedOperationException("TODO");
    }
    
    public static boolean isCapsuleTemplate(IClass clazz)
    {
        // TODO: Make this less brittle by using the annotations' fully qualified names.
        Predicate<Annotation> isCapsuleAnnotation = (a -> CAPSULE_ANNOTATION_NAME.equals(a.getType()
                                                                                 .getName()
                                                                                 .getClassName()
                                                                                 .toString()));
        return clazz.getName()
                    .toString()
                    .endsWith(CAPSULE_TEMPLATE_SUFFIX)
            && clazz.getAnnotations()
                    .stream()
                    .anyMatch(isCapsuleAnnotation);
    }

    public static boolean templateIsActive(IClass clazz)
    {
        return clazz.getAllMethods()
                    .stream()
                    .filter(m ->isRelevantTemplateMethod(m))
                    .anyMatch(m -> isRunDecl(m));
    }
    
    public static boolean isRelevantTemplateMethod(IMethod method)
    {
        String prefix = "< Application,";
        return method.toString().startsWith(prefix);
    }
    
    public static boolean isProcedure(IMethod method)
    {
        return isCapsuleTemplate(method.getDeclaringClass())  // A procedures must be on a capsule.
            && method.isPrivate() == false  // A capsule's private methods are not procedures.
            && isSpecialCapsuleDecl(method) == false;  // Don't count special decls as procedures.
    }
    
    public static boolean isSpecialCapsuleDecl(IMethod method)
    {
        return isInitDecl(method)
            || isDesignDecl(method)
            || isRunDecl(method);
    }
    
    public static boolean isInitDecl(IMethod method)
    {
        return isNamed(method, "init");
    }
    
    public static boolean isDesignDecl(IMethod method)
    {
        return isNamed(method, "design");
    }

    public static boolean isRunDecl(IMethod method)
    {
        return isNamed(method, "run")
            && method.getReturnType().equals(TypeReference.Void)
            && method.getNumberOfParameters() == 1;  // The one and only parameter is `this`.
    }
    
    /**
     * Returns true iff the "local" capsule template, when it calls the given method is invoking
     * a procedure on a remote capsule.
     * 
     * @param local a reference to a capsule template
     * @param invokedMethod a method invoked at some point by local.
     */
    public static boolean isRemoteProcedure(IClass local, IMethod invokedMethod)
    {
        // TODO: This implementation assumes that if the "local" capsule template is calling a
        // method on a capsule with the same type, then "local" is making a local procedure call.
        // This is incorrect in the following case: the "local" capsule has a reference to a remote
        // instance which just so happens to have the same type. Essentially, this fails whenever
        // capsule definitions are recursive.
        
        assert isCapsuleTemplate(local);
        return local.equals(invokedMethod.getDeclaringClass()) == false
            && isProcedure(invokedMethod);
    }
}
