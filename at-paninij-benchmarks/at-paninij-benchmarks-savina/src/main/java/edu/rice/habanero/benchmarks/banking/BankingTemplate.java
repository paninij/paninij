package edu.rice.habanero.benchmarks.banking;

import org.paninij.benchmarks.savina.util.FlagFuture;
import org.paninij.lang.Capsule;
import org.paninij.lang.Child;


@Capsule public class BankingTemplate {
    @Child Teller teller;
    @Child static final Account[] accounts = new Account[BankingConfig.A];

    public void design(Banking self) {
        teller.wire(accounts);
        for (Account a : accounts) a.wire(teller, accounts);
    }

    public void run() {
        FlagFuture wait = teller.start();
        wait.block();
    }
}
