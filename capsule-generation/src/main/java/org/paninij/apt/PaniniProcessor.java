/*
 * This file is part of the Panini project at Iowa State University.
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributor(s): Dalton Mills, David Johnston, Trey Erenberger
 */
package org.paninij.apt;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
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
import org.paninij.apt.util.Reporter;
import org.paninij.apt.util.Source;
import org.paninij.lang.Capsule;
import org.paninij.lang.Signature;
import org.paninij.model.ElementCapsule;
import org.paninij.model.Procedure;
import org.paninij.model.Variable;


/**
 * Used as a service during compilation to make automatically-generated `.java` files from classes
 * annotated with one of the annotations in `org.paninij.lang`.
 */
@SupportedAnnotationTypes({ "org.paninij.lang.Capsule", "org.paninij.lang.Signature" })
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class PaniniProcessor extends AbstractProcessor
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

        for (Element elem : annotated) {

            if (CapsuleChecker.check(this, elem)) {

                TypeElement template = (TypeElement) elem;

                org.paninij.model.Capsule cap = ElementCapsule.make(template);

                ArrayList<Procedure> procs = cap.getProcedures();
                System.out.println("# " + cap.getSimpleName());
                for (int i = 0; i < procs.size(); i++) {
                    System.out.print("--" + procs.get(i).getName() + "(");
                    String args = "";
                    for (Variable v : procs.get(i).getParameters()) {
                        args += (v + ", ");
                    }
                    args = args.length() > 1 ? args.substring(0, args.length() - 2) : "";
                    System.out.print(args + ")\n");
                }

                MakeCapsule.make(this, template).makeSourceFile();
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

    String getPackageOf(TypeElement type) {
        Elements utils = processingEnv.getElementUtils();
        Name pkg = utils.getPackageOf(type).getQualifiedName();
        return pkg.toString();
    }

    String getPackageOf(TypeMirror type) {
        Types utils = processingEnv.getTypeUtils();
        return getPackageOf((TypeElement) utils.asElement(type));
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
