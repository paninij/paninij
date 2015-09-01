package edu.rice.habanero.benchmarks.banking;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;


@Capsule public class BankingTemplate {
    @Local Teller teller;

    public void run() {
        teller.start();
    }
}
