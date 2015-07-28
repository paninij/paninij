package edu.rice.habanero.benchmarks.banking;

import org.paninij.lang.Capsule;
import org.paninij.lang.Child;


@Capsule public class BankingTemplate {
    @Child Teller teller;

    public void run() {
        teller.start();
    }
}
