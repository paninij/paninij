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

import static org.paninij.apt.util.PaniniModel.CAPSULE_TEMPLATE_SUFFIX;
import static java.util.Collections.singleton;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
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

import org.paninij.apt.check.CapsuleChecker;
import org.paninij.apt.check.CapsuleTestChecker;
import org.paninij.apt.check.SignatureChecker;
import org.paninij.apt.check.StaticOwnershipTransfer;
import org.paninij.apt.check.StaticOwnershipTransfer.Kind;
import org.paninij.apt.model.Capsule;
import org.paninij.apt.model.CapsuleElement;
import org.paninij.apt.model.Procedure;
import org.paninij.apt.model.Signature;
import org.paninij.apt.model.SignatureElement;
import org.paninij.apt.util.ArtifactCompiler;
import org.paninij.apt.util.ArtifactFiler;
import org.paninij.apt.util.ArtifactMaker;
import org.paninij.apt.util.UserArtifact;
import org.paninij.runtime.check.DynamicOwnershipTransfer;
import org.paninij.soter.SoterAnalysis;
import org.paninij.soter.SoterAnalysisFactory;
import org.paninij.soter.instrument.SoterInstrumenter;
import org.paninij.soter.instrument.SoterInstrumenterFactory;
import org.paninij.soter.util.WalaUtil;


/**
 * Used as a service during compilation to make automatically-generated `.java` files from classes
 * annotated with one of the annotations in `org.paninij.lang`.
 */
@SupportedAnnotationTypes({"org.paninij.lang.Capsule",
                           "org.paninij.lang.Signature",
                           "org.paninij.lang.CapsuleTester"})
@SupportedOptions({"panini.classPath",
                   "panini.classPathFile",
                   "panini.sourcePath",
                   "panini.classOutput",
                   "panini.sourceOutput",
                   DynamicOwnershipTransfer.ARGUMENT_KEY,
                   StaticOwnershipTransfer.ARGUMENT_KEY,
                   "panini.soter.analysisReports",
                   "panini.soter.callGraphPDFs",
                   "panini.soter.heapGraphPDFs"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class PaniniProcessor extends AbstractProcessor
{
    protected RoundEnvironment roundEnv;
    protected ProcessorOptions options;
    protected ArtifactMaker artifactMaker;
    protected SoterAnalysisFactory soterAnalysisFactory;
    protected SoterInstrumenterFactory soterInstrumenterFactory;
    protected boolean midpointCompile;
    
    @Override
    public void init(ProcessingEnvironment procEnv)
    {
        super.init(procEnv);

        try
        {
            options = new ProcessorOptions(procEnv.getOptions());
            note(options.toString());
            
            // Note that the artifact compiler should not perform annotation processing.
            if (options.staticOwnershipTransferKind == Kind.SOTER)
            {
                artifactMaker = ArtifactCompiler.make(procEnv.getFiler(), options,
                                                      singleton("-proc:none"));
            }
            else
            {
                artifactMaker = ArtifactFiler.make(procEnv.getFiler(), options);
            }

        }
        catch (IOException ex) {
            throw new RuntimeException("Failed to make artifact compiler: " + ex, ex);
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        note("Starting a round of processing for annotations: " + annotations.toString());
        this.roundEnv = roundEnv;

        // Sets which contain models
        Set<Capsule> capsules = new HashSet<Capsule>();
        Set<Signature> signatures = new HashSet<Signature>();
        Set<Capsule> capsuleTests = new HashSet<Capsule>();

        // Collect all Signature models
        for (Element elem : roundEnv.getElementsAnnotatedWith(org.paninij.lang.Signature.class))
        {
            // Note: `getElementsAnnotatedWith()` even returns elements which inherit `@Signature`.
            //       This includes capsule artifacts generated in a prior round which implement a
            //       user-defined signature.
            if (elem.getAnnotation(org.paninij.lang.Signature.class) != null && SignatureChecker.check(this, elem)) {
                TypeElement template = (TypeElement) elem;
                signatures.add(SignatureElement.make(template));
            }
        }

        // Collect all Capsule models
        for (Element elem : roundEnv.getElementsAnnotatedWith(org.paninij.lang.Capsule.class))
        {
            if (CapsuleChecker.check(this, elem)) {
                TypeElement template = (TypeElement) elem;
                capsules.add(CapsuleElement.make(template));
                artifactMaker.add(new UserArtifact(template.getQualifiedName().toString()));
            }
        }

        // Collect all CapsuleTest capsule models
        for (Element elem : roundEnv.getElementsAnnotatedWith(org.paninij.lang.CapsuleTest.class))
        {
            if (CapsuleTestChecker.check(this, elem)) {
                TypeElement template = (TypeElement) elem;
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

        // Generate artifacts from signature model
        for (Signature signature : signatures)
        {
            // Generate Messages
            for (Procedure procedure : signature.getProcedures()) {
                artifactMaker.add(messageFactory.make(procedure));
            }

            // Generate the mangled signature.
            artifactMaker.add(signatureFactory.make(signature));
        }
        
        // Generate capsule artifacts
        for (Capsule capsule : capsules)
        {
            // Generate Messages
            for (Procedure procedure : capsule.getProcedures()) {
                artifactMaker.add(messageFactory.make(procedure));
            }

            // Generate capsule interface
            artifactMaker.add(capsuleInterfaceFactory.make(capsule));
            
            // Generate dummy capsule
            artifactMaker.add(capsuleDummyFactory.make(capsule));
        }
        
        if (options.staticOwnershipTransferKind == Kind.SOTER)
        {
            artifactMaker.makeAll();
            analyzeAndInstrument(capsules);
        }
       
        for (Capsule capsule : capsules)
        {
            // Generate capsule thread profile
            artifactMaker.add(threadCapsuleFactory.make(capsule));

            // TODO Generate other capsule profiles
        }

        // Generate capsule test artifacts
        for (Capsule capsuleTest : capsuleTests)
        {
            // Generate Messages
            for (Procedure procedure : capsuleTest.getProcedures())
            {
                artifactMaker.add(messageFactory.make(procedure));
            }

            // Generate the capsule test's interface artifact.
            artifactMaker.add(capsuleInterfaceFactory.make(capsuleTest));

            // Generate the capsule test's `$Thread` artifact.
            artifactMaker.add(threadCapsuleFactory.make(capsuleTest));

            // Generate capsule test artifact
            artifactMaker.add(capsuleTestFactory.make(capsuleTest));
        }
        
        artifactMaker.makeAll();

        this.roundEnv = null;  // Release reference, so that the `roundEnv` can potentially be GC'd.
        note("Finished a round of processing.");

        return false;
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

    public void log(String logFilePath, String logMsg)
    {
        try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(logFilePath, true))))
        {
            out.println(logMsg);
        }
        catch (IOException ex)
        {
            throw new RuntimeException("Failed to log a message: " + ex, ex);
        }
    }
    
    protected void analyzeAndInstrument(Set<Capsule> capsules)
    {
        assert options.staticOwnershipTransferKind == Kind.SOTER;
        
        // Note that instantiation of the analysis factory needs to happen after the artifacts have
        // been compiled so that the bytecode for those artifacts will be found by the CHA.
        soterAnalysisFactory = new SoterAnalysisFactory(options.effectiveClassPathString);
        soterInstrumenterFactory = new SoterInstrumenterFactory(options.classOutput.getAbsolutePath());

        for (Capsule capsule : capsules)
        {
            String capsuleName = capsule.getQualifiedName();
            SoterAnalysis soterAnalysis = soterAnalysisFactory.make(capsuleName);
            soterAnalysis.perform();

            // TODO: Make this actually use the user's directory.
            if (options.analysisReports != null)
            {
                log(options.analysisReports.getAbsolutePath() + File.separator + capsuleName,
                    soterAnalysis.getResultsReport());
            }

            SoterInstrumenter soterInstrumenter = soterInstrumenterFactory.make(soterAnalysis);
            soterInstrumenter.perform();

            if (options.callGraphPDFs != null)
            {
                String callGraphPDF = options.callGraphPDFs.getAbsolutePath() + File.separator + capsuleName + ".pdf";
                WalaUtil.makeGraphFile(soterAnalysis.getCallGraph(), callGraphPDF);
            }
    
            if (options.heapGraphPDFs != null)
            {
                String heapGraphPDF = options.heapGraphPDFs.getAbsolutePath() + File.separator + capsuleName + ".pdf";
                WalaUtil.makeGraphFile(soterAnalysis.getHeapGraph(), heapGraphPDF);
            }
        }
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
