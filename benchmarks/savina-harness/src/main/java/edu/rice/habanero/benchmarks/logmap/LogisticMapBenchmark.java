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
package edu.rice.habanero.benchmarks.logmap;

import org.paninij.benchmarks.savina.util.BenchmarkSuite;

public class LogisticMapBenchmark
{
    public static void main(String[] args) {
//        LogisticMapScalaActorBenchmark.main(args); -- hangs
        BenchmarkSuite.mark("LogisticMap");

        LogisticMapScalaManualStashActorBenchmark.main(args);
        LogisticMapScalazActorBenchmark.main(args);

        LogisticMapAtPaniniJTaskBenchmark.main(args);
        LogisticMapAtPaniniJBenchmark.main(args);
        LogisticMapAtPaniniJSerialBenchmark.main(args);



//        LogisticMapScalazManualStashActorBenchmark.main(args); -- error out

        LogisticMapAkkaAwaitActorBenchmark.main(args);

//        LogisticMapAkkaBecomeActorBenchmark.main(args); -- hangs

        LogisticMapAkkaBecomeExtActorBenchmark.main(args);
        LogisticMapAkkaManualStashActorBenchmark.main(args);

//        LogisticMapFuncJavaActorBenchmark.main(args); -- error out

        LogisticMapFuncJavaManualStashActorBenchmark.main(args);
        LogisticMapGparsManualStashActorBenchmark.main(args);
        LogisticMapHabaneroManualStashActorBenchmark.main(args);
        LogisticMapHabaneroManualStashSelectorBenchmark.main(args);
        LogisticMapHabaneroPauseResumeActorBenchmark.main(args);
        LogisticMapHabaneroPauseResumeSelectorBenchmark.main(args);
        LogisticMapHabaneroSelectorBenchmark.main(args);
        LogisticMapJetlangActorBenchmark.main(args);

//        LogisticMapJetlangManualStashActorBenchmark.main(args); -- error out
    }
}
