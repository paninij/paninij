package org.paninij.apt;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import org.paninij.apt.util.DuckShape;
import org.paninij.apt.util.PaniniModelInfo;
import org.paninij.apt.util.Source;
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
    Set<DuckShape> foundDuckShapes = new HashSet<DuckShape>();

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        this.roundEnv = roundEnv;

        for (Element elem : roundEnv.getElementsAnnotatedWith(Signature.class)) {
            if (SignatureChecker.check(this, elem)) {
                // Nothing to do for now.
            }
        }

        Set<? extends Element> annotated = roundEnv.getElementsAnnotatedWith(Capsule.class);

        for (Element elem : annotated)
        {
            if (CapsuleChecker.check(this, elem))
            {
                TypeElement template = (TypeElement) elem;

                printCapsuleDeclInfo(template);

                MakeCapsule.make(this, template).makeSourceFile();

                //MakeCapsule.make(this, template).makeSourceFile();
                MakeCapsule$Thread.make(this, template).makeSourceFile();
                //MakeCapsule$Task.make(this, template).makeSourceFile();
                //MakeCapsule$Monitor.make(this, template).makeSourceFile();
                //MakeCapsule$Serial.make(this, template).makeSourceFile();

                MakeDucks.make(this, template).makeDucks();
            }
        }

        this.roundEnv = null;
        return false;
    }

    public void printCapsuleDeclInfo(TypeElement template)
    {
        System.out.println();
        System.out.println(Source.format("printCapsuleDeclInfo(#0): ", template));

        List<VariableElement> capsules = PaniniModelInfo.getCapsuleFieldDecls(this, template);
        System.out.println(Source.format("#0 capsules: #1", capsules.size(), capsules.toString()));

        List<VariableElement> children = PaniniModelInfo.getChildFieldDecls(this, template);
        System.out.println(Source.format("#0 children: #1", children.size(), children.toString()));

        List<VariableElement> reqs = PaniniModelInfo.getWiredFieldDecls(this, template);
        System.out.println(Source.format("#0 requirements: #1", reqs.size(), reqs.toString()));
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

    public Types getTypeUtils() {
        return processingEnv.getTypeUtils();
    }

    public Elements getElementUtils() {
        return processingEnv.getElementUtils();
    }
}
