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
package edu.rice.habanero.benchmarks.piprecision;

import org.paninij.benchmarks.savina.util.BenchmarkSuite;

public class PiPrecisionBenchmark
{
    public static void main(String[] args) {
        BenchmarkSuite.mark("PiPrecision");
        PiPrecisionScalaActorBenchmark.main(args);
        PiPrecisionScalazActorBenchmark.main(args);
        PiPrecisionAkkaActorBenchmark.main(args);
        PiPrecisionFuncJavaActorBenchmark.main(args);
        PiPrecisionGparsActorBenchmark.main(args);
        PiPrecisionHabaneroActorBenchmark.main(args);
        PiPrecisionHabaneroSelectorBenchmark.main(args);
        PiPrecisionJetlangActorBenchmark.main(args);
        PiPrecisionJumiActorBenchmark.main(args);
        PiPrecisionAtPaniniJBenchmark.main(args);
        PiPrecisionAtPaniniJTaskBenchmark.main(args);
        PiPrecisionAtPaniniJSerialBenchmark.main(args);
        PiPrecisionLiftActorBenchmark.main(args);
    }
}
