package org.paninij.soter;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.types.TypeReference;

import static org.paninij.soter.JavaModel.*;

public class PaniniModel
{
    public static boolean isCapsuleTemplate(IClass clazz)
    {
        throw new UnsupportedOperationException("TODO");
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
        return method.isPrivate() == false     // A capsule's private methods are not procedures.
            && isCapsuleDecl(method) == false;
    }
    
    public static boolean isCapsuleDecl(IMethod method) {
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
}
