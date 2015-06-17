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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import org.paninij.apt.check.CapsuleChecker;
import org.paninij.apt.check.CapsuleTesterChecker;
import org.paninij.apt.check.SignatureChecker;
import org.paninij.apt.util.SourceFile;
import org.paninij.lang.Capsule;
import org.paninij.lang.CapsuleTest;
import org.paninij.lang.Signature;
import org.paninij.model.CapsuleElement;
import org.paninij.model.Procedure;
import org.paninij.runtime.check.Panini$Ownership;
import org.paninij.model.SignatureElement;


/**
 * Used as a service during compilation to make automatically-generated `.java` files from classes
 * annotated with one of the annotations in `org.paninij.lang`.
 */
@SupportedAnnotationTypes({"org.paninij.lang.Capsule",
                           "org.paninij.lang.Signature",
                           "org.paninij.lang.CapsuleTester"})
@SupportedOptions({"ownership.check.method", "foo"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class PaniniProcessor extends AbstractProcessor
{
    RoundEnvironment roundEnv;
    Panini$Ownership.CheckMethod ownershipCheckMethod;
    
    @Override
    public void init(ProcessingEnvironment procEnv)
    {
        super.init(procEnv);
        initOptions(procEnv.getOptions());
    }
    

    protected void initOptions(Map<String, String> options)
    {
        note("Annotation Processor Options: " + options);
        initOwnershipCheckMethod(options);
    }
    
    protected void initOwnershipCheckMethod(Map<String, String> options)
    {
        String opt = options.get(Panini$Ownership.CheckMethod.getArgumentKey());
        if (opt == null)
        {
            ownershipCheckMethod = Panini$Ownership.CheckMethod.getDefault();
            note("No `ownership.check.method` annotation processor argument given. Using default.");
        } else {
            // Throws exception if `opt` is invalid:
            ownershipCheckMethod = Panini$Ownership.CheckMethod.fromString(opt);
        }
        note("Using ownership.check.method = " + ownershipCheckMethod);
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        this.roundEnv = roundEnv;

        MessageFactory messageFactory = new MessageFactory();
        SignatureFactory signatureFactory = new SignatureFactory();
        CapsuleFactory capsuleFactory = new CapsuleFactory();

        for (Element elem : roundEnv.getElementsAnnotatedWith(Signature.class))
        {
            if (SignatureChecker.check(this, elem)) {
                TypeElement template = (TypeElement) elem;
                org.paninij.model.Signature signature = SignatureElement.make(template);
                SourceFile source = signatureFactory.make(signature);
                this.createJavaFile(source);
            }
        }

        for (Element elem : roundEnv.getElementsAnnotatedWith(Capsule.class))
        {
            if (CapsuleChecker.check(this, elem)) {

                TypeElement template = (TypeElement) elem;

                org.paninij.model.Capsule capsule = CapsuleElement.make(template);
                MakeCapsule.make(this, template, capsule).makeSourceFile();
                MakeCapsule$Thread.make(this, template, capsule).makeSourceFile();

                // this could be a part of CapsuleGenerator
                for (Procedure procedure : capsule.getProcedures()) {
                    SourceFile source = messageFactory.make(procedure);
                    this.createJavaFile(source);
                }
            }
        }
        
        for (Element elem : roundEnv.getElementsAnnotatedWith(CapsuleTest.class))
        {
            if (CapsuleTesterChecker.check(this, elem))
            {
                TypeElement template = (TypeElement) elem;
                org.paninij.model.Capsule capsule = CapsuleElement.make(template);
                MakeCapsuleTest.make(this, template, capsule).makeSourceFile();

                for (Procedure procedure : capsule.getProcedures()) {
                    SourceFile source = messageFactory.make(procedure);
                    this.createJavaFile(source);
                }
            }
        }

        this.roundEnv = null;  // Release reference, so that the `roundEnv` can potentially be GC'd.
        return false;
    }


    void createJavaFile(SourceFile source) {
        if (source != null) {
            this.createJavaFile(source.filename, source.content);
        }
    }

    /**
     * @param cls The fully qualified name of the class that will go in the newly created file.
     * @param src The source to be put in the newly create java file.
     */
    void createJavaFile(String cls, String src) {
        try {
            JavaFileObject file = processingEnv.getFiler().createSourceFile(cls);
            file.openWriter().append(src).close();
        } catch (IOException e) {
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

    public void note(String msg) {
        //processingEnv.getMessager().printMessage(Kind.NOTE, "--- " + msg);
        System.out.println("--- " + msg);
    }

    public void warning(String msg) {
        //processingEnv.getMessager().printMessage(Kind.WARNING, "~~~ " + msg);
        System.out.println("~~~ " + msg);
    }

    public void error(String msg) {
        processingEnv.getMessager().printMessage(Kind.ERROR, "!!! " + msg);
    }

    public Types getTypeUtils() {
        return processingEnv.getTypeUtils();
    }

    public Elements getElementUtils() {
        return processingEnv.getElementUtils();
    }
}
