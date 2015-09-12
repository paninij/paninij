package edu.rice.habanero.benchmarks.banking;

import org.paninij.lang.Capsule;
import org.paninij.lang.Imports;

@Capsule public class AccountTemplate {

    @Imports Teller teller;
    @Imports Account[] accounts = new Account[BankingConfig.A];

    double balance = BankingConfig.INITIAL_BALANCE;

    public void done() {
        for (Account a : accounts) a.exit();
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
