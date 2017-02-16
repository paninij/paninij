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

import org.paninij.lang.Capsule;
import org.paninij.lang.Imported;

@Capsule public class AccountCore {

    @Imported Teller teller;
    @Imported Account[] accounts = new Account[BankingConfig.A];

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
