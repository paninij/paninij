package edu.rice.habanero.benchmarks.concdict;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;

@Capsule public class MasterTemplate {

    @Local Worker[] workers = new Worker[DictionaryConfig.NUM_ENTITIES];
    @Local Dictionary dictionary;

    int numWorkersTerminated = 0;

    public void design(Master self) {
        for (int i = 0; i < workers.length; i++) {
            workers[i].imports(self, dictionary, i);
        }
        dictionary.imports(workers);
    }

    public void start() {
        for (Worker w : workers) w.doWork();
    }

    public void workerFinished() {
        numWorkersTerminated++;
        if (numWorkersTerminated == DictionaryConfig.NUM_ENTITIES) {
            dictionary.printResult();
            for (Worker w : workers) w.exit();
        }
    }
}
