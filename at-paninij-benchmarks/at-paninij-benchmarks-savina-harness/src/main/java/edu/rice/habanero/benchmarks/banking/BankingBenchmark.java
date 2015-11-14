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
package edu.rice.habanero.benchmarks.banking;

import org.paninij.benchmarks.savina.util.BenchmarkSuite;

public class BankingBenchmark
{
    public static void main(String[] args) {
        BenchmarkSuite.mark("Banking");
        BankingScalaActorBenchmark.main(args);
        BankingScalaManualStashActorBenchmark.main(args);

        BankingScalazActorBenchmark.main(args);
//        BankingScalazManualStashActorBenchmark.main(args); -- throws error

        BankingAkkaAwaitActorBenchmark.main(args);
        BankingAkkaBecomeActorBenchmark.main(args);
        BankingAkkaBecomeExtActorBenchmark.main(args);
        BankingAkkaManualStashActorBenchmark.main(args);

        BankingFuncJavaActorBenchmark.main(args);
//        BankingFuncJavaManualStashActorBenchmark.main(args); -- throws error

        BankingGparsManualStashActorBenchmark.main(args);

        BankingHabaneroManualStashActorBenchmark.main(args);
        BankingHabaneroPauseResumeActorBenchmark.main(args);

        BankingHabaneroManualStashSelectorBenchmark.main(args);
        BankingHabaneroPauseResumeSelectorBenchmark.main(args);

        BankingHabaneroSelectorBenchmark.main(args);

        BankingJetlangActorBenchmark.main(args);
//        BankingJetlangManualStashActorBenchmark.main(args); -- throws error

        BankingJumiManualStashActorBenchmark.main(args);

        BankingAtPaniniJBenchmark.main(args);
        BankingAtPaniniJTaskBenchmark.main(args);
        BankingAtPaniniJSerialBenchmark.main(args);

        BankingLiftManualStashActorBenchmark.main(args);
    }
}
