package edu.rice.habanero.benchmarks.banking;

import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

@Capsule public class AccountTemplate {

    @Wired Teller teller;
    @Wired Account[] accounts = new Account[BankingConfig.A];

    double balance = BankingConfig.INITIAL_BALANCE;

    public void done() {
        for (Account a : accounts) a.exit();
        teller.exit();
    }

    public void debit(double amount) {
        balance += amount;
        teller.transactionComplete();
    }

    public void credit(double amount, int dest) {
        balance -= amount;
        accounts[dest].debit(amount);
    }

}
