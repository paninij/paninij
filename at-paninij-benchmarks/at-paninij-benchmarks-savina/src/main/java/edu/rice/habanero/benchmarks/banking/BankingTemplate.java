package edu.rice.habanero.benchmarks.banking;

import java.util.concurrent.ExecutionException;

import org.paninij.benchmarks.savina.util.FlagFuture;
import org.paninij.lang.Capsule;
import org.paninij.lang.Child;


@Capsule public class BankingTemplate {
    @Child Teller teller;
    @Child Account[] accounts = new Account[BankingConfig.A];

    public void design(Banking self) {
        teller.wire(accounts);
        for (Account a : accounts) a.wire(teller);
    }

    public void run() {
        FlagFuture wait = teller.start();
        try {
            wait.get();
        } catch (InterruptedException | ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
