package org.paninij.soter.util;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IClassLoader;
import com.ibm.wala.classLoader.IField;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.cha.IClassHierarchy;
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
     * TODO: This procedure should be deprecated in favor of some sort of check which uses fully
     * qualified annotation names, or even better, WALA resolved annotations.
     * 
     * @param field
     * @param name The simple (i.e. short, unqualified) name of an annotation.
     */
    public static boolean hasAnnotationNamed(IField field, String name)
    {
        Collection<Annotation> annotations = field.getAnnotations();
        if (annotations == null) {
            return false;
        }
        return annotations.stream().anyMatch(a -> isNamed(a, name));
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
    

    public static boolean isApplicationMethod(IMethod method)
    {
        // TODO: Make this less brittle.
        return method.toString().startsWith("< Application,");
    }

    
    public static Iterator<IClass> getApplicationClassesIterator(IClassHierarchy classHierarchy)
    {
        IClassLoader appLoader = classHierarchy.getLoader(ClassLoaderReference.Application);
        return appLoader.iterateAllClasses();
    }
    

    public static String getApplicationClassesString(IClassHierarchy classHierarchy)
    {
        StringBuilder appClasses = new StringBuilder();
        Iterator<IClass> classIter = getApplicationClassesIterator(classHierarchy);
        while (classIter.hasNext()) {
            appClasses.append(" " + classIter.next() + "\n");
        }
        return appClasses.toString();
    }


    /**
     * @param clazz
     * @return A list of all of the "application" (i.e. not "primordial") methods on the template.
     */
    public static Stream<IMethod> getApplicationMethods(IClass clazz)
    {
        return clazz.getAllMethods()
                    .stream()
                    .filter(m -> isApplicationMethod(m));
    }


    /**
     * TODO: Should be deprecated in favor of using `getApplicationMethods()` directly.
     * 
     * @param clazz
     * @return A list of all of the "application" (i.e. not "primordial") methods on the template.
     */
    public static List<IMethod> getApplicationMethodsList(IClass clazz)
    {
        return getApplicationMethods(clazz).collect(toList());
    }
}
