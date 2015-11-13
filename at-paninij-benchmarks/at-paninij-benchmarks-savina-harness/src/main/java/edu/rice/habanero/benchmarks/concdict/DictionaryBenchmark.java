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
package edu.rice.habanero.benchmarks.concdict;

import org.paninij.benchmarks.savina.util.BenchmarkSuite;

public class DictionaryBenchmark
{
    public static void main(String[] args) {
        BenchmarkSuite.mark("Dictionary");
        DictionaryScalaActorBenchmark.main(args);
        DictionaryScalazActorBenchmark.main(args);
        DictionaryAkkaActorBenchmark.main(args);
        DictionaryFuncJavaActorBenchmark.main(args);
        DictionaryGparsActorBenchmark.main(args);
        DictionaryHabaneroActorBenchmark.main(args);
        DictionaryHabaneroSelectorBenchmark.main(args);
        DictionaryJetlangActorBenchmark.main(args);
        DictionaryJumiActorBenchmark.main(args);
        DictionaryAtPaniniJBenchmark.main(args);
        DictionaryAtPaniniJTaskBenchmark.main(args);
//        DictionaryAtPaniniJSerialBenchmark.main(args); -- StackOverflowError
        DictionaryLiftActorBenchmark.main(args);
    }
}
