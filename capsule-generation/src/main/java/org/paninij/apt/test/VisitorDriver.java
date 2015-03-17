package org.paninij.apt.test;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import org.paninij.apt.util.TypeCollector;


/**
 * A processor for running tests on various kinds of visitors defined in the `PaniniPress` project.
 * 
 * Because of the current lack of better alternatives, this is just being used for manual
 * inspection of the results of visitors (e.g. `TypeCollector`).
 */
@SupportedAnnotationTypes({"org.paninij.apt.test.Visit"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class VisitorDriver extends AbstractProcessor
{
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        //note("VisitorDriver.process()");
        testTypeCollector(roundEnv);
        return false;
    }
    
    private void testTypeCollector(RoundEnvironment roundEnv)
    {
        //printAllTypeCollectorResults(roundEnv);
    }
    
    private void printAllTypeCollectorResults(RoundEnvironment roundEnv)
    {
        note("VisitorDriver.testTypeCollector() - results:");
        for (Element elem : roundEnv.getElementsAnnotatedWith(Visit.class))
        {
            for (String result : TypeCollector.collect(elem)) {
                note(result);
            }
        }
    }

    void note(String msg) {
        //System.out.println(msg);
        processingEnv.getMessager().printMessage(Kind.NOTE, msg);
    }

    void warning(String msg) {
        processingEnv.getMessager().printMessage(Kind.WARNING, msg);
    }

    void error(String msg) {
        processingEnv.getMessager().printMessage(Kind.ERROR, msg);
    }
}
