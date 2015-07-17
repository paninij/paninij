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
import javax.tools.JavaFileObject;

import org.paninij.apt.check.CapsuleChecker;
import org.paninij.apt.check.CapsuleTestChecker;
import org.paninij.apt.check.SignatureChecker;
import org.paninij.apt.util.PaniniArtifactCompiler;
import org.paninij.apt.util.PaniniModelInfo;
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
@SupportedOptions({"ownership.check.method",
                   "panini.soter",
                   "panini.class.path",
                   "panini.class.path.file",
                   "panini.source.path",
                   "panini.class.output",
                   "panini.source.output"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class PaniniProcessor extends AbstractProcessor
{
    // Annotation processor options (i.e. `-A` arguments):
    public static Panini$Ownership.CheckMethod ownershipCheckMethod;

    // Annotation processor options (i.e. `-A` arguments):
    protected boolean initializedWithOptions = false;
    protected boolean soterEnabled;
    protected PaniniArtifactCompiler artifactCompiler;

    RoundEnvironment roundEnv;

    @Override
    public void init(ProcessingEnvironment procEnv)
    {
        note("init()");
        super.init(procEnv);

        Map<String, String> options = procEnv.getOptions();
        initWithOptions(options);
    }
    
    public void initWithOptions(Map<String, String> options)
    {
        // If this already been initialized, then ignore this attempt with these options.
        if (initializedWithOptions)
            return;

        initOwnershipOptions(options);
        note("Annotation Processor Options: " + options);

        soterEnabled = options.containsKey("panini.soter") ? true : false;
        if (soterEnabled)
        {
            try
            {
                artifactCompiler = PaniniArtifactCompiler.makeFromProcessorOptions(options);
            }
            catch (IOException e)
            {
                warning("Failed to make the panini processor's artifact compiler. Disabling SOTER.");
                soterEnabled = false;
            }
        }

        // Consider this processor initialized.
        initializedWithOptions = true;
    }
    
    protected void initOwnershipOptions(Map<String, String> options)
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
        note("Starting a round of processing for annotations: " + annotations.toString());
        this.roundEnv = roundEnv;

        // Sets which contain models
        Set<org.paninij.model.Capsule> capsules = new HashSet<org.paninij.model.Capsule>();
        Set<org.paninij.model.Signature> signatures = new HashSet<org.paninij.model.Signature>();
        Set<org.paninij.model.Capsule> capsuleTests = new HashSet<org.paninij.model.Capsule>();

        // Collect all Signature models
        for (Element elem : roundEnv.getElementsAnnotatedWith(Signature.class))
        {
            // Note: `getElementsAnnotatedWith()` even returns elements which inherit `@Signature`.
            //       This includes capsule artifacts generated in a prior round which implement a
            //       user-defined signature.
            if (elem.getAnnotation(Signature.class) != null && SignatureChecker.check(this, elem)) {
                TypeElement template = (TypeElement) elem;
                signatures.add(SignatureElement.make(template));
            }
        }

        // Collect all Capsule models
        for (Element elem : roundEnv.getElementsAnnotatedWith(Capsule.class))
        {
            if (CapsuleChecker.check(this, elem)) {
                TypeElement template = (TypeElement) elem;
                capsules.add(CapsuleElement.make(template));
            }
        }

        // Collect all CapsuleTest capsule models
        for (Element elem : roundEnv.getElementsAnnotatedWith(CapsuleTest.class))
        {
            if (CapsuleTestChecker.check(this, elem)) {
                TypeElement template = (TypeElement) elem;
                capsules.add(CapsuleElement.make(template));
                capsuleTests.add(CapsuleElement.make(template));
            }
        }

        // Artifact factories
        MessageFactory messageFactory = new MessageFactory();
        SignatureFactory signatureFactory = new SignatureFactory();
        CapsuleInterfaceFactory capsuleInterfaceFactory = new CapsuleInterfaceFactory();
        CapsuleDummyFactory capsuleDummyFactory = new CapsuleDummyFactory();
        CapsuleTestFactory capsuleTestFactory = new CapsuleTestFactory();
        CapsuleThreadFactory threadCapsuleFactory = new CapsuleThreadFactory();

        SourceFile sourceFile;  // A temporary variable.
        Set<String> toBeCompiled = new HashSet<String>();
        

        // Generate artifacts from signature model
        for (org.paninij.model.Signature signature : signatures)
        {
            // The original signature needs to be compiled.
            toBeCompiled.add(signature.getQualifiedName());
            
            // Generate Messages
            for (Procedure procedure : signature.getProcedures()) {
                this.createJavaFile(messageFactory.make(procedure));
            }

            // Generate the mangled signature.
            sourceFile = signatureFactory.make(signature);
            this.createJavaFile(sourceFile);
            toBeCompiled.add(sourceFile.qualifiedName);
        }
        
        // Generate capsule artifacts
        for (org.paninij.model.Capsule capsule : capsules)
        {
            // Add the capsule template itself:
            if (capsuleTests.contains(capsule) == false) {
                toBeCompiled.add(capsule.getQualifiedName() + PaniniModelInfo.CAPSULE_TEMPLATE_SUFFIX);
            }
            
            // Generate Messages
            for (Procedure procedure : capsule.getProcedures()) {
                this.createJavaFile(messageFactory.make(procedure));
            }

            // Generate capsule interface
            sourceFile = capsuleInterfaceFactory.make(capsule);
            this.createJavaFile(sourceFile);
            toBeCompiled.add(sourceFile.qualifiedName);
            
            // Generate dummy capsule
            sourceFile = capsuleDummyFactory.make(capsule);
            this.createJavaFile(sourceFile);
            toBeCompiled.add(sourceFile.qualifiedName);
        }
        
        if (soterEnabled)
        {
            try {
                artifactCompiler.compileAll(toBeCompiled);
            } catch (IOException e) {
                e.printStackTrace();
                throw new IllegalStateException("Failed to compile.");
            }
        }
        
        for (org.paninij.model.Capsule capsule : capsules)
        {
            // Generate capsule thread profile
            this.createJavaFile(threadCapsuleFactory.make(capsule));

            // TODO Generate other capsule profiles
        }

        // Generate capsule test artifacts
        for (org.paninij.model.Capsule capsule : capsuleTests)
        {
            // Generate Messages
            for (Procedure procedure : capsule.getProcedures()) {
                this.createJavaFile(messageFactory.make(procedure));
            }

            // Generate capsule test artifact
            this.createJavaFile(capsuleTestFactory.make(capsule));
        }

        this.roundEnv = null;  // Release reference, so that the `roundEnv` can potentially be GC'd.
        note("Finished a round of processing.");

        return false;
    }

    void createJavaFile(SourceFile source)
    {
        if (source != null) {
            this.createJavaFile(source.qualifiedName, source.content);
        }
    }

    /**
     * @param cls The fully qualified name of the class that will go in the newly created file.
     * @param src The source to be put in the newly create java file.
     */
    void createJavaFile(String cls, String src)
    {
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
        System.out.println("--- PaniniProcessor: " + msg);
    }

    public void warning(String msg) {
        System.out.println("~~~ PaniniProcessor: " + msg);
    }

    public void error(String msg) {
        processingEnv.getMessager().printMessage(javax.tools.Diagnostic.Kind.ERROR,
                                                 "!!! PaniniProcessor: " + msg);
    }

    public Types getTypeUtils() {
        return processingEnv.getTypeUtils();
    }

    public Elements getElementUtils() {
        return processingEnv.getElementUtils();
    }
}
