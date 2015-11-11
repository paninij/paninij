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
package org.paninij.proc;

import static org.paninij.proc.check.FailureBehavior.LOGGING;
import static org.paninij.proc.check.FailureBehavior.EXCEPTION;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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

import org.paninij.proc.check.CapsuleTestChecker;
import org.paninij.proc.check.FailureBehavior;
import org.paninij.proc.check.Result;
import org.paninij.proc.check.capsule.CapsuleCheckException;
import org.paninij.proc.check.capsule.CapsuleChecker;
import org.paninij.proc.check.signature.SignatureCheckException;
import org.paninij.proc.check.signature.SignatureChecker;
import org.paninij.proc.model.Capsule;
import org.paninij.proc.model.CapsuleElement;
import org.paninij.proc.model.Procedure;
import org.paninij.proc.model.Signature;
import org.paninij.proc.model.SignatureElement;
import org.paninij.proc.util.ArtifactFiler;
import org.paninij.proc.util.ArtifactMaker;
import org.paninij.proc.util.UserArtifact;


/**
 * Used as an annotation processor service during compilation to make automatically-generated
 * `.java` files from classes annotated with one of the annotations in `org.paninij.lang`.
 */
@SupportedAnnotationTypes({"org.paninij.lang.Capsule",
                           "org.paninij.lang.Signature",
                           "org.paninij.lang.CapsuleTester"})
@SupportedOptions({"panini.capsuleListFile",
                   "panini.exceptOnFailedChecks"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class PaniniProcessor extends AbstractProcessor
{
    protected RoundEnvironment roundEnv;
    protected ArtifactMaker artifactMaker;
    protected String capsuleListFile;
    protected FailureBehavior failureBehavior;
    
    @Override
    public void init(ProcessingEnvironment processingEnv)
    {
        super.init(processingEnv);

        Map<String,String> options = processingEnv.getOptions();
        capsuleListFile = options.get("panini.capsuleListFile");
        failureBehavior = options.containsKey("panini.exceptOnFailedChecks") ? EXCEPTION : LOGGING;
        
        artifactMaker = new ArtifactFiler(processingEnv.getFiler()) ;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        this.roundEnv = roundEnv;
        
        // Sets which contain models.
        Set<Capsule> capsules = new HashSet<Capsule>();
        Set<Signature> signatures = new HashSet<Signature>();
        Set<Capsule> capsuleTests = new HashSet<Capsule>();

        // Check each signature template, and make its `Signature` model.
        SignatureChecker signatureChecker = new SignatureChecker(processingEnv, roundEnv);
        for (Element elem : roundEnv.getElementsAnnotatedWith(org.paninij.lang.Signature.class))
        {
            Result checkResult = signatureChecker.check(this, elem);
            if (checkResult.ok()) {
                TypeElement template = (TypeElement) elem;
                signatures.add(SignatureElement.make(template));
            } else {
                switch (failureBehavior) {
                case EXCEPTION:
                    throw new SignatureCheckException(checkResult.err());
                case LOGGING:
                default:
                    error(checkResult.err(), elem);
                }
            }
        }

        // Check each capsule template, and make its `Capsule` model.
        CapsuleChecker templateChecker = new CapsuleChecker(processingEnv, roundEnv);
        for (Element elem : roundEnv.getElementsAnnotatedWith(org.paninij.lang.Capsule.class))
        {
            Result checkResult = templateChecker.check(this, elem);
            if (checkResult.ok()) {
                TypeElement template = (TypeElement) elem;
                capsules.add(CapsuleElement.make(template));
                artifactMaker.add(new UserArtifact(template.getQualifiedName().toString()));
            } else {
                switch (failureBehavior) {
                case EXCEPTION:
                    throw new CapsuleCheckException(checkResult.err());
                case LOGGING:
                default:
                    error(checkResult.err(), elem);
                }
            }
        }

        // Check each capsule test template, and make its capsule model.
        for (Element elem : roundEnv.getElementsAnnotatedWith(org.paninij.lang.CapsuleTest.class))
        {
            if (CapsuleTestChecker.check(this, elem)) {
                TypeElement template = (TypeElement) elem;
                capsuleTests.add(CapsuleElement.make(template));
            }
        }
        
        // End early if appropriate.
        if (capsules.isEmpty() && signatures.isEmpty() && capsuleTests.isEmpty()) {
            return false;
        }

        // Make artifact factories.
        MessageFactory messageFactory = new MessageFactory();
        SignatureFactory signatureFactory = new SignatureFactory();
        CapsuleInterfaceFactory capsuleInterfaceFactory = new CapsuleInterfaceFactory();
        CapsuleMockupFactory capsuleMockupFactory = new CapsuleMockupFactory();
        CapsuleTestFactory capsuleTestFactory = new CapsuleTestFactory();
        CapsuleThreadFactory threadCapsuleFactory = new CapsuleThreadFactory();
        CapsuleSerialFactory serialCapsuleFactory = new CapsuleSerialFactory();
        CapsuleMonitorFactory monitorCapsuleFactory = new CapsuleMonitorFactory();
        CapsuleTaskFactory taskCapsuleFactory = new CapsuleTaskFactory();

        // Generate artifacts from each `Signature` model.
        for (Signature signature : signatures)
        {
            // Generate messages.
            for (Procedure procedure : signature.getProcedures()) {
                artifactMaker.add(messageFactory.make(procedure));
            }

            // Generate the mangled signature interface.
            artifactMaker.add(signatureFactory.make(signature));
            
            // Generate a mockup capsule implementing the signature interface.
            artifactMaker.add(capsuleMockupFactory.make(signature));
        }
        
        // Generate artifacts from each `Capsule` model.
        for (Capsule capsule : capsules)
        {
            // Generate messages.
            for (Procedure procedure : capsule.getProcedures()) {
                artifactMaker.add(messageFactory.make(procedure));
            }

            // Generate the mangled capsule interface.
            artifactMaker.add(capsuleInterfaceFactory.make(capsule));
            
            // Generate mockup capsule implementing the capsule interface.
            artifactMaker.add(capsuleMockupFactory.make(capsule));

            // Generate the capsules with four different thread profiles
            artifactMaker.add(threadCapsuleFactory.make(capsule));
            artifactMaker.add(serialCapsuleFactory.make(capsule));
            artifactMaker.add(monitorCapsuleFactory.make(capsule));
            artifactMaker.add(taskCapsuleFactory.make(capsule));
        }
        
        // Generate capsule test artifacts
        for (Capsule capsuleTest : capsuleTests)
        {
            // Generate messages.
            for (Procedure procedure : capsuleTest.getProcedures()) {
                artifactMaker.add(messageFactory.make(procedure));
            }

            // Generate the capsule test's interface artifact.
            artifactMaker.add(capsuleInterfaceFactory.make(capsuleTest));

            // Generate the capsule test's `$Thread` artifact.
            artifactMaker.add(threadCapsuleFactory.make(capsuleTest));

            // Generate capsule test artifact itself.
            artifactMaker.add(capsuleTestFactory.make(capsuleTest));
        }
        
        artifactMaker.makeAll();
        artifactMaker.close();

        if (capsuleListFile != null) {
            makeCapsuleListFile(capsules);
        }

        this.roundEnv = null;  // Release reference, so that the `roundEnv` can potentially be GC'd.

        return false;
    }
    
    
    protected void makeCapsuleListFile(Set<Capsule> capsules)
    {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(capsuleListFile, false));
            for (Capsule capsule : capsules) {
                writer.append(capsule.getQualifiedName() + "\n");
            }
            writer.close();
        }
        catch (IOException ex) {
            throw new RuntimeException("Cannot open the SOTER arguments file: " + capsuleListFile);
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

    public void log(String logFilePath, String logMsg, boolean append)
    {
        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(logFilePath, append))))
        {
            out.println(logMsg);
        }
        catch (IOException ex)
        {
            throw new RuntimeException("Failed to log a message: " + ex, ex);
        }
    }

    
    public void note(String msg) {
        System.out.println("--- PaniniProcessor: " + msg);
    }

    public void warning(String msg) {
        System.out.println("~~~ PaniniProcessor: " + msg);
    }

    public void error(String msg) {
        processingEnv.getMessager().printMessage(javax.tools.Diagnostic.Kind.ERROR, "!!! " + msg);
    }

    public void error(String msg, Element elem) {
        processingEnv.getMessager().printMessage(javax.tools.Diagnostic.Kind.ERROR, msg, elem);
    }

    public Types getTypeUtils() {
        return processingEnv.getTypeUtils();
    }

    public Elements getElementUtils() {
        return processingEnv.getElementUtils();
    }
}
