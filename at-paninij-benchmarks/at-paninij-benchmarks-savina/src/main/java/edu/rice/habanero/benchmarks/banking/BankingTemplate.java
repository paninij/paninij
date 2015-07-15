package edu.rice.habanero.benchmarks.banking;

import org.paninij.benchmarks.savina.util.FlagFuture;
import org.paninij.lang.Capsule;
import org.paninij.lang.Child;


@Capsule public class BankingTemplate {
    @Child Teller teller;

    public void run() {
        FlagFuture wait = teller.start();
        wait.block();
    }
}
