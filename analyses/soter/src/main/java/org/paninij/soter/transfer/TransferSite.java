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

import javax.json.JsonObject;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAReturnInstruction;
import com.ibm.wala.util.intset.IntSet;

public abstract class TransferSite
{
    /**
     * @param node          The call graph node in which this transfer
     * @param transferInstr The instruction (from the SSA IR of `node`) which performs the transfer.
     * @param transfers     A set of value numbers (from the SSA IR of `node`) which are
     *                      transferred at this transfer site. If there are no transfers, then that
     *                      should be represented by passing `null`.
     */
    public static TransferSite make(CGNode node, IntSet transfers, SSAInstruction transferInstr)
    {
        switch (Kind.fromSSAInstruction(transferInstr)) {
        case INVOKE:
        case RETURN:
            return new ReturnTransferSite(node, transfers, transferInstr);
        default:
            String msg = "Cannot make a transfer site because `transferInstr` kind is unknown.";
            throw new RuntimeException(msg);
        }
    }
    
    protected final CGNode node;
    protected final IntSet transfers;
    protected final SSAInstruction transferInstr;
    
    public TransferSite(CGNode node, IntSet transfers, SSAInstruction transferInstr)
    {
        assert node != null;
        assert transfers == null || ! transfers.isEmpty();
        assert transferInstr != null;

        this.node = node;
        this.transferInstr = transferInstr;
        this.transfers = transfers;
    }
    
    public abstract Kind getKind();
    
    public abstract JsonObject toJson();
    
    public CGNode getNode()
    {
        return node;
    }
    
    public SSAInstruction getInstruction()
    {
        return transferInstr;
    }
    
    /**
     * @return An IntSet of the SSA IR value numbers which are potentially-unsafe transfers (or
     *         `null` if there are none).
     */
    public IntSet getTransfers()
    {
        return transfers;
    }

    public static enum Kind
    {
        INVOKE,
        RETURN;
        
        public static Kind fromSSAInstruction(SSAInstruction instr)
        {
            if (instr instanceof SSAAbstractInvokeInstruction)
                return INVOKE;
            if (instr instanceof SSAReturnInstruction)
                return RETURN;
            
            String msg = "The given instruction is not a known transfer site kind: " + instr;
            throw new IllegalArgumentException(msg);
        }
    }
}
