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
 *  Dr. Hridesh Rajan,
 *  Dalton Mills,
 *  David Johnston,
 *  Trey Erenberger
 *  Jackson Maddox
 *******************************************************************************/
package org.paninij.test;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * <p>This processor is a helper class for running single user-defined task in every round of a
 * Java compilation task. In any round, the user's task will always be passed the current
 * {@link ProcessingEnvironment} and {@link RoundEnvironment}.
 *
 * <p>Instances of this class are <em>universal processors</em> (as the term is used in the docs of
 * {@link Processor}): An instance will claim all annotation types ({@code "*"}), and it will be run
 * even if all root elements of a given round have no annotations on them.
 *
 * @see Processor
 * @author dwtj on 2/25/16.
 */
@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class UniversalProcessor extends AbstractProcessor {

    private final BiConsumer<ProcessingEnvironment, RoundEnvironment> task;

    public UniversalProcessor(BiConsumer<ProcessingEnvironment, RoundEnvironment> task) {
        this.task = task;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        task.accept(processingEnv, roundEnv);
        return false;
    }
}

