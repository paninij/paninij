package edu.rice.habanero.benchmarks.banking;

import java.util.Random;

import org.paninij.benchmarks.savina.util.FlagFuture;
import org.paninij.lang.Capsule;
import org.paninij.lang.Child;

@Capsule public class TellerTemplate {
    @Child Account[] accounts = new Account[BankingConfig.A];

    int numCompletedBankings = 0;
    Random randomGen = new Random(123456);
    int numBankings = BankingConfig.N;
    FlagFuture flag = new FlagFuture();

    public void design(Teller self) {
        for (Account a : accounts) a.wire(self, accounts);
    }

    public FlagFuture start() {
        for (int i = 0; i < numBankings; i++) generateWork();
        return flag;
    }

    public void transactionComplete() {
        numCompletedBankings++;
        if (numCompletedBankings == numBankings) {
            for (Account a : accounts) {
                a.done();
                a.exit();
            }
            flag.resolve();
        }
    }

    private void generateWork() {
        int srcAccountId = randomGen.nextInt((accounts.length / 10) * 8);
        int loopId = randomGen.nextInt(accounts.length - srcAccountId);

        if (loopId == 0) loopId++;
        int destAccountId = srcAccountId + loopId;
        Account srcAccount = accounts[srcAccountId];
        double amount = Math.abs(randomGen.nextDouble() * 1000);
        srcAccount.credit(amount, destAccountId);
    }

}
