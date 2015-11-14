/*******************************************************************************
 * This file is part of the Panini project at Iowa State University.
 *
 * @PaniniJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * @PaniniJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with @PaniniJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * For more details and the latest version of this code please see
 * http://paninij.org
 *
 * Contributors:
 * 	Dr. Hridesh Rajan,
 * 	Dalton Mills,
 * 	David Johnston,
 * 	Trey Erenberger
 *******************************************************************************/
package edu.rice.habanero.benchmarks.banking;

import java.util.Random;

import org.paninij.lang.Capsule;
import org.paninij.lang.Local;

@Capsule public class TellerTemplate {
    @Local Account[] accounts = new Account[BankingConfig.A];

    int numCompletedBankings = 0;
    Random randomGen = new Random(123456);
    int numBankings = BankingConfig.N;

    public void design(Teller self) {
        for (Account a : accounts) a.imports(self, accounts);
    }

    public void start() {
        for (int i = 0; i < numBankings; i++) generateWork();
    }

    public void transactionComplete() {
        numCompletedBankings++;
        if (numCompletedBankings == numBankings) {
            for (Account a : accounts) {
                a.done();
                a.exit();
            }
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
