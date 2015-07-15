package org.paninij.soter;

import java.io.IOException;
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
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import org.paninij.apt.util.PaniniModelInfo;
import org.paninij.lang.CapsuleInterface;
import org.paninij.soter.util.WalaUtil;

import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.WalaException;

/**
 * This annotation processor is triggered by the presence of capsule artifacts created by
 * `PaniniProcessor`. It looks for a corresponding capsule template and performs the SOTER analysis
 * on that template. The `panini.classpath` is required to point to
 * 
 *  - The location of the capsule template class (e.g. `bin/` or `target/classes`)
 *  - The location of the `@PaniniJ` runtime classes (e.g. lib/at-paninij-runtime.jar)
 */
@SupportedAnnotationTypes({"org.paninij.lang.CapsuleInterface",
                           "org.paninij.lang.CapsuleTest",
                           "org.paninij.lang.CapsuleThread",
                           "org.paninij.lang.CapsuleDummy",
                           "org.paninij.lang.Capsule"})
@SupportedOptions({"panini.classpath"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class SoterProcessor extends AbstractProcessor
{
    // Annotation processor options (i.e. `-A` arguments):
    public static boolean isEnabled = false;
    public static String paniniClasspath;

    @Override
    public void init(ProcessingEnvironment procEnv)
    {
        note("init()");
        super.init(procEnv);
        initOptions(procEnv.getOptions());
    }

    protected void initOptions(Map<String, String> options)
    {
        note("Annotation Processor Options: " + options);
        initWalaClassPath(options);
    }

    protected void initWalaClassPath(Map<String, String> options)
    {
        paniniClasspath = options.get("panini.classpath");
        if (paniniClasspath == null) {
            note("No `panini.classpath` annotation processor argument given, so SOTER is disabled.");
        } else {
            note("Using panini.classpath = " + paniniClasspath);
            isEnabled = true;
        }
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        if (isEnabled)
        {
            note("Starting a round of processing for annotations: " + annotations.toString());
            for (Element elem : roundEnv.getElementsAnnotatedWith(CapsuleInterface.class))
            {
                // If there is a capsule interface, assume that there is a corresponding template.
                String name = elem.toString();
                note("Analyzing "+ name);
                String path = WalaUtil.fromQualifiedNameToWalaPath(name)
                            + PaniniModelInfo.CAPSULE_TEMPLATE_SUFFIX;
                PaniniAnalysis analysis = new PaniniAnalysis(path, paniniClasspath);

                try {
                    analysis.perform();
                } catch (IllegalArgumentException | WalaException | CancelException | IOException e) {
                    e.printStackTrace();
                    throw new IllegalStateException("Failed to perform the panini analysis.");
                }

                note(analysis.getResultString());
            }
            note("Finished a round of processing.");
        }

        return false;
    }
    
    public void note(String msg) {
        System.out.println("--- SoterProcessor: " + msg);
    }

    public void warning(String msg) {
        System.out.println("~~~ SoterProcessor: " + msg);
    }

    public void error(String msg) {
        processingEnv.getMessager().printMessage(Kind.ERROR, "!!! SoterProcessor: " + msg);
    }
}
