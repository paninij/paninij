package edu.rice.habanero.benchmarks.banking;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;
import org.paninij.lang.Root;

@Root
@Capsule
public class BankingTemplate
{
    @Local Teller teller;

    public void run() {
        teller.start();
    }
}
