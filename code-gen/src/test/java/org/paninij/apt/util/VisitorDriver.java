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
 * Contributor(s): David Johnston
 */
package org.paninij.apt.util;

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
        //note("VisitorDriver.process():");
        testTypeCollector(roundEnv);
        return false;
    }

    private void testTypeCollector(RoundEnvironment roundEnv)
    {
        //note("VisitorDriver.testTypeCollector():");
        //printAllTypeCollectorResults(roundEnv);
    }

    /**
     * Method is only intended to be used for debugging (not for printing import statements).
     */
    @SuppressWarnings("unused")
    private void printAllTypeCollectorResults(RoundEnvironment roundEnv)
    {
        note("VisitorDriver.printAllTypeCollectorResults():");
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
