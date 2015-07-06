package edu.rice.habanero.benchmarks.fjcreate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule public class FjCreateTemplate {

    @Child Worker[] workers = new Worker[ForkJoinConfig.N];

    public void run() {
        List<Future<Void>> tasks = new ArrayList<Future<Void>>();

        for (Worker w : workers) tasks.add(w.process());

        try {
            for (Future<Void> f : tasks) f.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

}
