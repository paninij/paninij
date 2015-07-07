package edu.rice.habanero.benchmarks.banking;

import org.paninij.lang.Capsule;
import org.paninij.lang.Wired;

@Capsule public class AccountTemplate {

    @Wired Teller teller;

    double balance = BankingConfig.INITIAL_BALANCE;

    public void debit(Transaction t) {
        balance += t.getAmount();
        teller.debitTransactionComplete();
    }

    public void credit(Transaction t) {
        balance -= t.getAmount();
        teller.creditTransactionComplete(t);
    }

}
