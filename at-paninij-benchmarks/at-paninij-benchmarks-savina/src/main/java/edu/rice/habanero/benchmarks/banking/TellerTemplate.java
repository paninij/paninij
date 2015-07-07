package edu.rice.habanero.benchmarks.banking;

import java.util.Random;

import org.paninij.benchmarks.savina.util.FlagFuture;
import org.paninij.lang.Capsule;
import org.paninij.lang.Child;
import org.paninij.lang.Wired;

@Capsule public class TellerTemplate {
    @Wired Account[] accounts = new Account[BankingConfig.A];

    int numCompletedBankings = 0;
    Random randomGen = new Random(123456);
    int numBankings = BankingConfig.N;
    FlagFuture flag = new FlagFuture();

    public FlagFuture start() {
        for (int i = 0; i < numBankings; i++) generateWork();
        return flag;
    }

    public void creditTransactionComplete(Transaction t) {
        accounts[t.getDest()].debit(t);
    }

    public void debitTransactionComplete() {
        numCompletedBankings++;
        if (numCompletedBankings == numBankings) {
            flag.resolve();
            for (Account a : accounts) a.exit();
        }
    }

    private void generateWork() {
        int srcAccountId = randomGen.nextInt((accounts.length / 10) * 8);
        int loopId = randomGen.nextInt(accounts.length - srcAccountId);

        if (loopId == 0) loopId++;

        int destAccountId = srcAccountId + loopId;

        Account srcAccount = accounts[srcAccountId];

        double amount = Math.abs(randomGen.nextDouble() * 1000);

        srcAccount.credit(new Transaction(srcAccountId, destAccountId, amount));
    }

}
