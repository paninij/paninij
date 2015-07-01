package edu.rice.habanero.benchmarks.fjthrput;

import org.paninij.lang.Capsule;

@Capsule public class WorkerTemplate
{
    int messagesProcessed = 0;

    public void process() {
        messagesProcessed++;
        ThroughputConfig.performComputation(37.2);
    }
}
