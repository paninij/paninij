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
package edu.rice.habanero.benchmarks.fjcreate;

import org.paninij.benchmarks.savina.util.BenchmarkSuite;

public class ForkJoinBenchmark
{
    public static void main(String[] args) {
        BenchmarkSuite.mark("ForkJoin");
        ForkJoinScalaActorBenchmark.main(args);
        ForkJoinScalazActorBenchmark.main(args);
        ForkJoinAkkaActorBenchmark.main(args);
        ForkJoinFuncJavaActorBenchmark.main(args);
        ForkJoinGparsActorBenchmark.main(args);
        ForkJoinHabaneroActorBenchmark.main(args);
        ForkJoinHabaneroSelectorBenchmark.main(args);
        ForkJoinJetlangActorBenchmark.main(args);
        ForkJoinJumiActorBenchmark.main(args);
        ForkJoinAtPaniniJBenchmark.main(args);
        ForkJoinAtPaniniJTaskBenchmark.main(args);
        ForkJoinAtPaniniJSerialBenchmark.main(args);
        ForkJoinLiftActorBenchmark.main(args);
    }
}
