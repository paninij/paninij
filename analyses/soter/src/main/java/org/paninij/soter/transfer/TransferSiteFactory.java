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
package org.paninij.soter.transfer;

import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;
import com.ibm.wala.ssa.SSAReturnInstruction;
import com.ibm.wala.util.intset.IntSet;

public class TransferSiteFactory
{
    public static TransferSite copyWith(TransferSite orig, IntSet transfers)
    {
        if (orig instanceof InvokeTransferSite)
        {
            return new InvokeTransferSite(orig.getNode(),
                                          transfers,
                                          (SSAAbstractInvokeInstruction) orig.getInstruction());
        }
        if (orig instanceof ReturnTransferSite)
        {
            return new ReturnTransferSite(orig.getNode(),
                                          transfers,
                                          (SSAReturnInstruction) orig.getInstruction());
        }

        String msg = "Failed to copy the transfer site because its type is unknown: " + orig;
        throw new IllegalArgumentException(msg);
    }
}
