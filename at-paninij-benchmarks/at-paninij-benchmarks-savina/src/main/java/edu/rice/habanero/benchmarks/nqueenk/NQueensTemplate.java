package edu.rice.habanero.benchmarks.nqueenk;

import org.paninij.benchmarks.savina.util.FlagFuture;
import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

import edu.rice.habanero.benchmarks.BenchmarkRunner;

@Capsule public class NQueensTemplate {
    @Child Master master;

    public void run() {
        FlagFuture wait = master.start();
        wait.block();

        long expSolution = NQueensConfig.SOLUTIONS[NQueensConfig.SIZE - 1];
        long actSolution = master.getResult();
        int solutionsLimit = NQueensConfig.SOLUTIONS_LIMIT;
        boolean valid = actSolution >= solutionsLimit && actSolution <= expSolution;

        System.out.printf(BenchmarkRunner.argOutputFormat, "Solutions found", actSolution);
        System.out.printf(BenchmarkRunner.argOutputFormat, "Result valid", valid);

    }
}
