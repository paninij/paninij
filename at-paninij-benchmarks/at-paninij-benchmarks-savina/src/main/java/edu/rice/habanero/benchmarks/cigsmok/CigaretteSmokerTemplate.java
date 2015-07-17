package edu.rice.habanero.benchmarks.cigsmok;

import java.util.concurrent.ExecutionException;

import org.paninij.benchmarks.savina.util.FlagFuture;
import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule public class CigaretteSmokerTemplate {
    @Child Arbiter arbiter;

    public void run() {
        FlagFuture wait = arbiter.start();
        while (!wait.isDone());
//        try {
//            wait.get();
//        } catch (InterruptedException | ExecutionException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }
}
