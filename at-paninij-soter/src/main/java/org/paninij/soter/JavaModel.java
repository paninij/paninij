package org.paninij.soter;

import java.util.Iterator;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IClassLoader;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.annotations.Annotation;

public class JavaModel
{
    public static boolean isNamed(IMethod method, String name)
    {
        return method.getName().toString().equals(name);
    }
    
    /**
     * TODO: This procedure should be deprecated in favor of some sort of check which uses fully
     * qualified annotation names, or even better, WALA resolved annotations.
     * 
     * @param clazz
     * @param name The simple (i.e. short, unqualified) name of an annotation.
     */
    public static boolean hasAnnotationNamed(IClass clazz, String name)
    {
        return clazz.getAnnotations()
                    .stream()
                    .anyMatch(a -> isNamed(a, name));
    }
    
    /**
     * @param name The simple (i.e. short, unqualified) name of an annotation.
     */
    public static boolean isNamed(Annotation annotation, String name)
    {
        String actualName = annotation.getType()
                                      .getName()
                                      .getClassName()
                                      .toString();

        return name.equals(actualName);
    }
    
    public static Iterator<IClass> iterateAllApplicationClasses(ClassHierarchy cha)
    {
        IClassLoader appLoader = cha.getLoader(ClassLoaderReference.Application);
        return appLoader.iterateAllClasses();
    }
}
