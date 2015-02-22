package org.paninij.apt;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import org.paninij.lang.Panini;
import org.paninij.util.Source;


/**
 * Used as a service during compilation to make automatically-generated `.java` files from classes
 * annotated with one of the annotations in `org.paninij.lang`.
 */
@SupportedAnnotationTypes("org.paninij.lang.Panini")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class PaniniPress extends AbstractProcessor
{
    RoundEnvironment roundEnv;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        this.roundEnv = roundEnv;

        Set<? extends Element> annotated = roundEnv.getElementsAnnotatedWith(Panini.class);

        for (Element elem : annotated)
        {
            if (CapsuleChecker.check(this, elem) == true)
            {
                TypeElement template = (TypeElement) elem;

                //MakeCapsule.make(this, template).makeSourceFile();
                MakeCapsule$Thread.make(this, template).makeSourceFile();
                //MakeCapsule$Task.make(this, template).makeSourceFile();
                //MakeCapsule$Monitor.make(this, template).makeSourceFile();
                //MakeCapsule$Serial.make(this, template).makeSourceFile();
            } else {
                error("Capsule failed check.");
            }
        }

        this.roundEnv = null;
        return false;
    }


    /**
     * Static helper methods
     */

    static boolean hasVoidReturnType(ExecutableElement exec) {
        return exec.getReturnType().getKind() == TypeKind.VOID;
    }
    

    static boolean hasPrimitiveReturnType(ExecutableElement exec) {
        return exec.getReturnType().getKind().isPrimitive();
    }
    

    /**
     * Gives a string representation of the executable element's return type. If the return type is
     * a primitive (e.g. int, double, etc.), then the returned type will be that primitive's boxed
     * type (e.g. Integer, Double, etc.).
     * 
     * In the case that the executable element return has no return type (i.e. `void`), then "void"
     * is returned.
     * 
     * An `IllegalArgumentException` is raised whenever the `TypeKind` of `exec`'s return value is
     * any of the following:
     * 
     *  - NONE
     *  - NULL
     *  - ERROR
     *  - WILDCARD
     *  - PACKAGE
     *  - EXECUTABLE
     *  - OTHER
     *  - UNION
     *  - INTERSECTION
     * 
     * @param exec
     * @return A string of the executable's return type.
     */
    private String getBoxedReturnType(ExecutableElement exec)
    {
        switch (exec.getReturnType().getKind()) {
        case BOOLEAN:
            return "Boolean";
        case BYTE:
            return "Byte";
        case SHORT:
            return "Short";
        case INT:
            return "Integer";
        case LONG:
            return "Long";
        case CHAR:
            return "Character";
        case FLOAT:
            return "Float";
        case DOUBLE:
            return "Double";
        case VOID:
            return "void";
        case ARRAY:
        case DECLARED:  // A class or interface type.
            return exec.getReturnType().toString();
        case NONE:
        case NULL:
        case ERROR:
        case TYPEVAR:
        case WILDCARD:
        case PACKAGE:
        case EXECUTABLE:
        case OTHER:
        case UNION:         // TODO: What are union and intersection types?
        case INTERSECTION:
        default:
            throw new IllegalArgumentException();
        }
    }


    /**
     * Dynamic helper methods.
     */

    private TypeElement getTypeElement(String className) {
        return processingEnv.getElementUtils().getTypeElement(className);
    }

    /**
     * @param cls The fully qualified name of the class that will go in the newly created file.
     * @param src The source to be put in the newly create java file.
     */
    void createJavaFile(String cls, String src)
    {
        try
        {
            JavaFileObject file = processingEnv.getFiler().createSourceFile(cls);
            file.openWriter().append(src).close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    String getPackageOf(TypeElement cls) {
        Elements utils = processingEnv.getElementUtils();
        Name pkg = utils.getPackageOf(cls).getQualifiedName();
        return pkg.toString();
    }

    void note(String msg) {
        processingEnv.getMessager().printMessage(Kind.NOTE, msg);
    }
    
    void warning(String msg) {
        processingEnv.getMessager().printMessage(Kind.WARNING, msg);
    }
    
    void error(String msg) {
        processingEnv.getMessager().printMessage(Kind.ERROR, msg);
    }
}
