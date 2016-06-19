/*******************************************************************************
 * This file is part of the Panini project at Iowa State University.
 *
 * @PaniniJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * @PaniniJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with @PaniniJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributors:
 * 	Dr. Hridesh Rajan,
 * 	Dalton Mills,
 * 	David Johnston,
 * 	Trey Erenberger
 *******************************************************************************/

package org.paninij.proc;

import static java.text.MessageFormat.format;

import static org.paninij.proc.check.FailureBehavior.LOGGING;
import static org.paninij.proc.check.FailureBehavior.EXCEPTION;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

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
import org.paninij.proc.factory.ArtifactFactory;
import org.paninij.proc.factory.CapsuleInterfaceFactory;
import org.paninij.proc.factory.CapsuleMockupFactory;
import org.paninij.proc.factory.CapsuleMonitorFactory;
import org.paninij.proc.factory.CapsuleSerialFactory;
import org.paninij.proc.factory.CapsuleTaskFactory;
import org.paninij.proc.factory.CapsuleTestFactory;
import org.paninij.proc.factory.CapsuleThreadFactory;
import org.paninij.proc.factory.MessageFactory;
import org.paninij.proc.factory.SignatureFactory;
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
            Result checkResult = signatureChecker.check(elem);
            if (checkResult.ok()) {
                TypeElement template = (TypeElement) elem;
                signatures.add(SignatureElement.make(template));
            } else {
                switch (failureBehavior) {
                case EXCEPTION:
                    throw new SignatureCheckException(checkResult.err());
                case LOGGING:
                default:
                    if(checkResult.offender() == null) {
                        error(checkResult.err(), elem);
                    } else {
                        error(checkResult);
                    }
                }
            }
        }

        // Check each capsule template, and make its `Capsule` model.
        CapsuleChecker templateChecker = new CapsuleChecker(processingEnv, roundEnv);
        for (Element elem : roundEnv.getElementsAnnotatedWith(org.paninij.lang.Capsule.class))
        {
            Result checkResult = templateChecker.check(elem);
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
                    if(checkResult.offender() == null) {
                        error(checkResult.err(), elem);
                    } else {
                        error(checkResult);
                    }
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

    public void error(String err, Element elem) {
        processingEnv.getMessager().printMessage(javax.tools.Diagnostic.Kind.ERROR, err, elem);
    }
    
    public void error(Result error) {
        processingEnv.getMessager().printMessage(javax.tools.Diagnostic.Kind.ERROR, error.err(), error.offender());
    }

    public Types getTypeUtils() {
        return processingEnv.getTypeUtils();
    }

    public Elements getElementUtils() {
        return processingEnv.getElementUtils();
    }
    
    public static String getGeneratedAnno(Class<? extends ArtifactFactory<?>> clazz) {
    	TimeZone tz = TimeZone.getTimeZone("UTC");
    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
    	df.setTimeZone(tz);
    	String iso = df.format(new Date());
    	return format("@Generated(value = \"{0}\", date = \"{1}\")", clazz.getName(), iso);
    }
}
