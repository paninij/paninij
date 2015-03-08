package org.paninij.apt;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import org.paninij.lang.Capsule;
import org.paninij.lang.Signature;


/**
 * Used as a service during compilation to make automatically-generated `.java` files from classes
 * annotated with one of the annotations in `org.paninij.lang`.
 */
@SupportedAnnotationTypes({ "org.paninij.lang.Capsule", "org.paninij.lang.Signature" })
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class PaniniPress extends AbstractProcessor
{
    RoundEnvironment roundEnv;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        this.roundEnv = roundEnv;

        for (Element elem : roundEnv.getElementsAnnotatedWith(Signature.class)) {
            if (SignatureChecker.check(this, elem)) {
                TypeElement signature = (TypeElement) elem;
                MakeSignature.make(this, signature).makeSourceFile();
            } else {
                // TODO better error message
                error("Signature failed check.");
            }
        }

        Set<? extends Element> annotated = roundEnv.getElementsAnnotatedWith(Capsule.class);

        for (Element elem : annotated)
        {
            if (CapsuleChecker.check(this, elem)) {
                TypeElement template = (TypeElement) elem;

                MakeCapsuleInterface.make(this, template).makeSourceFile();

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
