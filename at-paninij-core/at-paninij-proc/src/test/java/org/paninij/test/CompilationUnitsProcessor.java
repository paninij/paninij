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

import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.Trees;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * <p>This processor is a helper class for a running a processor in every round of a
 * compilation task and within that round, applying a single single user-defined task to the
 * round's root elements.
 *
 * <p>Instances of this class are <em>universal processors</em> (as the term is used in the docs of
 * {@link Processor})
 *
 * @see Processor
 * @see UniversalProcessor
 * @see RoundEnvironment#getRootElements
 *
 * @author dwtj
 */
@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
final public class CompilationUnitsProcessor extends UniversalProcessor {

    public CompilationUnitsProcessor(Consumer<CompilationUnitTree> task) {
        super((p, r) -> getTrees(p, r).forEach(task));
    }

    private static List<CompilationUnitTree> getTrees (ProcessingEnvironment procEnv,
                                                       RoundEnvironment roundEnv) {
        Trees treeUtils = Trees.instance(procEnv);
        return roundEnv.getRootElements().stream()
                .map(root -> treeUtils.getPath(root).getCompilationUnit())
                .collect(Collectors.toList());
    }
}
